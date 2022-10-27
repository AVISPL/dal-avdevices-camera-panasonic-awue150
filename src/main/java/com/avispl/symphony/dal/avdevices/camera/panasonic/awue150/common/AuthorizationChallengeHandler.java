package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.avispl.symphony.dal.util.StringUtils;

/**
 * DigestScheme
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public class AuthorizationChallengeHandler {
	/*
	 * RFC 2617 and 7616 specifies these characters to use when creating a hex string.
	 */
	private static final char[] HEX_CHARACTERS = "0123456789abcdef".toCharArray();

	private static final String BASIC = "Basic ";
	private static final String DIGEST = "Digest ";

	private static final String ALGORITHM = "algorithm";
	private static final String REALM = "realm";
	private static final String NONCE = "nonce";
	private static final String QOP = "qop";
	private static final String AUTH = "auth";
	private static final String AUTH_INT = "auth-int";
	private static final String USERHASH = "userhash";
	private static final String OPAQUE = "opaque";

	/*
	 * Digest proxy supports 3 unique algorithms in SHA-512/256, SHA-256, and MD5. Each algorithm is able to be used in
	 * a <algorithm> and <algorithm>-sess variant, if the '-sess' variant is sent the response nonce and generated
	 * cnonce (client nonce) will be used to calculate HA1.
	 */
	private static final String SESS = "-SESS";

	private static final String SHA_512_256 = "SHA-512-256";
	private static final String SHA_512_256_SESS = SHA_512_256 + SESS;

	private static final String SHA_256 = "SHA-256";
	private static final String SHA_256_SESS = SHA_256 + SESS;

	private static final String MD5 = "MD5";
	private static final String MD5_SESS = MD5 + SESS;

	private static final String[] ALGORITHM_PREFERENCE_ORDER = {
			SHA_512_256,
			SHA_512_256_SESS,
			SHA_256,
			SHA_256_SESS,
			MD5,
			MD5_SESS
	};

	/**
	 * Header representing a server requesting authentication.
	 */
	public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	/**
	 * Header representing the authorization the client is presenting to a server.
	 */
	public static final String AUTHORIZATION = "Authorization";

	private final String username;
	private final String password;
	private final Map<String, AtomicInteger> nonceTracker = new ConcurrentHashMap<>();
	private final AtomicReference<String> authorizationPipeliningType = new AtomicReference<>();
	private final AtomicReference<ConcurrentHashMap<String, String>> lastChallenge = new AtomicReference<>();

	/**
	 * Creates an {@link AuthorizationChallengeHandler} using the {@code username} and {@code password} to respond to
	 * authentication challenges.
	 *
	 * @param username Username used to response to authorization challenges.
	 * @param password Password used to respond to authorization challenges.
	 * @throws NullPointerException If {@code username} or {@code password} are {@code null}.
	 */
	public AuthorizationChallengeHandler(String username, String password) {
		this.username = Objects.requireNonNull(username, "'username' cannot be null.");
		this.password = Objects.requireNonNull(password, "'password' cannot be null.");
	}

	/**
	 * Handles Basic authentication challenges.
	 *
	 * @return Authorization header for Basic authentication challenges.
	 */
	public final String handleBasic() {
		authorizationPipeliningType.set(BASIC);
		String token = username + ":" + password;
		return BASIC + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Handles Digest authentication challenges.
	 *
	 * @param method HTTP method being used in the request.
	 * @param uri Relative URI for the request.
	 * @param challenges List of challenges that the server returned for the client to choose from and use when creating
	 * the authorization header.
	 * @param entityBodySupplier Supplies the request entity body, used to compute the hash of the body when using
	 * {@code "qop=auth-int"}.
	 * @return Authorization header for Digest authentication challenges.
	 */
	public final String handleDigest(String method, String uri, List<Map<String, String>> challenges,
			Supplier<byte[]> entityBodySupplier) {
		authorizationPipeliningType.set(DIGEST);
		Map<String, List<Map<String, String>>> challengesByType = partitionByChallengeType(challenges);

		for (String algorithm : ALGORITHM_PREFERENCE_ORDER) {
			// No challenges using this algorithm, skip it.
			if (!challengesByType.containsKey(algorithm)) {
				continue;
			}

			Function<byte[], byte[]> digestFunction = getDigestFunction(algorithm);

			// Unable to retrieve a digest for the specified algorithm, skip it.
			if (digestFunction == null) {
				continue;
			}

			ConcurrentHashMap<String, String> challenge = new ConcurrentHashMap<>(challengesByType.get(algorithm)
					.get(0));
			lastChallenge.set(challenge);

			return createDigestAuthorizationHeader(method, uri, challenge, algorithm, entityBodySupplier,
					digestFunction);
		}
		return null;
	}

	/**
	 * Parses the {@code Authorization} or {@code Authentication} header into its key-value pairs.
	 * <p>
	 * This will remove quotes on quoted string values.
	 *
	 * @param header Authorization or Authentication header.
	 * @return The Authorization or Authentication header split into its key-value pairs.
	 */
	public Map<String, String> parseAuthenticationOrAuthorizationHeader(String header) {
		if (StringUtils.isNullOrEmpty(header)) {
			return Collections.emptyMap();
		}

		if (header.startsWith(BASIC) || header.startsWith(DIGEST)) {
			header = header.split(" ", 2)[1];
		}

		return Stream.of(header.split(","))
				.map(String::trim)
				.map(kvp -> kvp.split("=", 2))
				.collect(Collectors.toMap(kvpPieces -> kvpPieces[0].toLowerCase(Locale.ROOT),
						kvpPieces -> kvpPieces[1].replace("\"", "")));
	}

	/**
	 * Creates the Authorization header for the Digest authentication challenge.
	 */
	private String createDigestAuthorizationHeader(String method, String uri, Map<String, String> challenge,
			String algorithm, Supplier<byte[]> entityBodySupplier, Function<byte[], byte[]> digestFunction) {
		String realm = challenge.get(REALM);
		String nonce = challenge.get(NONCE);
		String qop = getQop(challenge.get(QOP));
		String opaque = challenge.get(OPAQUE);
		boolean hashUsername = Boolean.parseBoolean(challenge.get(USERHASH));

		/**
		 * If the algorithm being used is <algorithm>-sess or QOP is 'auth' or 'auth-int' a client nonce will be needed
		 * to calculate the authorization header. If the QOP is set a nonce-count will need to retrieved.
		 */
		int nc = 0;
		String clientNonce = null;
		if (AUTH.equals(qop) || AUTH_INT.equals(qop)) {
			clientNonce = generateNonce();
			nc = getNc(challenge);
		} else if (algorithm.endsWith(SESS)) {
			clientNonce = generateNonce();
		}

		String ha1 = algorithm.endsWith(SESS)
				? calculateHa1Sess(digestFunction, realm, nonce, clientNonce)
				: calculateHa1NoSess(digestFunction, realm);

		String ha2 = AUTH_INT.equals(qop)
				? calculateHa2AuthIntQop(digestFunction, method, uri, entityBodySupplier.get())
				: calculateHa2AuthQopOrEmpty(digestFunction, method, uri);

		String response = (AUTH.equals(qop) || AUTH_INT.equals(qop))
				? calculateResponseKnownQop(digestFunction, ha1, nonce, nc, clientNonce, qop, ha2)
				: calculateResponseUnknownQop(digestFunction, ha1, nonce, ha2);

		String headerUsername = (hashUsername) ? calculateUserhash(digestFunction, realm) : username;

		return buildAuthorizationHeader(headerUsername, realm, uri, algorithm, nonce, nc, clientNonce, qop, response,
				opaque, hashUsername);
	}

	/**
	 * Retrieves the nonce count for the given challenge. If the nonce in the challenge has already been used this will
	 * increment and return the nonce count tracking, otherwise this will begin a new nonce tracking and return 1.
	 */
	private int getNc(Map<String, String> challenge) {
		String nonce = challenge.get(NONCE);
		if (nonceTracker.containsKey(nonce)) {
			return nonceTracker.get(nonce).incrementAndGet();
		} else {
			nonceTracker.put(nonce, new AtomicInteger(1));
			return 1;
		}
	}

	/**
	 * Parses the qopHeader for the qop to use. If the qopHeader is null or only contains unknown qop types null will
	 * be returned, otherwise the preference is 'auth' followed by 'auth-int'.
	 */
	private String getQop(String qopHeader) {
		if (StringUtils.isNullOrEmpty(qopHeader)) {
			return null;
		} else if (qopHeader.equalsIgnoreCase(AUTH)) {
			return AUTH;
		} else if (qopHeader.equalsIgnoreCase(AUTH_INT)) {
			return AUTH_INT;
		} else {
			return null;
		}
	}

	/**
	 * Calculates the 'HA1' hex string when using an algorithm that isn't a '-sess' variant.
	 *
	 * This performs the following operations:
	 * - Create the digest of (username + ":" + realm + ":" password).
	 * - Return the resulting bytes as a hex string.
	 */
	private String calculateHa1NoSess(Function<byte[], byte[]> digestFunction, String realm) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s:%s", username, realm, password)
				.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the 'HA1' hex string when using a '-sess' algorithm variant.
	 *
	 * This performs the following operations:
	 * - Create the digest of (username + ":" + realm + ":" password).
	 * - Convert the resulting bytes to a hex string, aliased as userPassHex.
	 * - Create the digest of (userPassHex + ":" nonce + ":" + cnonce).
	 * - Return the resulting bytes as a hex string.
	 */
	private String calculateHa1Sess(Function<byte[], byte[]> digestFunction, String realm, String nonce,
			String cnonce) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s:%s", calculateHa1NoSess(digestFunction, realm),
				nonce, cnonce).getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the 'HA2' hex string when using 'qop=auth' or the qop is unknown.
	 *
	 * This performs the following operations:
	 * - Create the digest of (httpMethod + ":" + uri).
	 * - Return the resulting bytes as a hex string.
	 */
	private String calculateHa2AuthQopOrEmpty(Function<byte[], byte[]> digestFunction, String httpMethod, String uri) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s", httpMethod, uri)
				.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the 'HA2' hex string when using 'qop=auth-int'.
	 *
	 * This performs the following operations:
	 * - Create the digest of (requestEntityBody).
	 * - Convert the resulting bytes to a hex string, aliased as bodyHex.
	 * - Create the digest of (httpMethod + ":" + uri + ":" bodyHex).
	 * - Return the resulting bytes as a hex string.
	 *
	 * The request entity body is the unmodified body of the request. Using 'qop=auth-int' requires the request body to
	 * be replay-able, this is why 'auth' is preferred instead of auth-int as this cannot be guaranteed. In addition to
	 * the body being replay-able this runs into risks when the body is very large and potentially consuming large
	 * amounts of memory.
	 */
	private String calculateHa2AuthIntQop(Function<byte[], byte[]> digestFunction, String httpMethod, String uri,
			byte[] requestEntityBody) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s:%s", httpMethod, uri,
				hexStringOf(digestFunction.apply(requestEntityBody))).getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the 'response' hex string when qop is unknown.
	 *
	 * This performs the following operations:
	 * - Create the digest of (ha1 + ":" + nonce + ":" + ha2).
	 * - Return the resulting bytes as a hex string.
	 */
	private String calculateResponseUnknownQop(Function<byte[], byte[]> digestFunction, String ha1, String nonce,
			String ha2) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s:%s", ha1, nonce, ha2)
				.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the 'response' hex string when 'qop=auth' or 'qop=auth-int'.
	 *
	 * This performs the following operations:
	 * - Create the digest of (ha1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2).
	 * - Return the resulting byes as a hex string.
	 *
	 * nc, nonce count, is represented in a hexadecimal format.
	 */
	private String calculateResponseKnownQop(Function<byte[], byte[]> digestFunction, String ha1, String nonce, int nc,
			String cnonce, String qop, String ha2) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s:%08X:%s:%s:%s", ha1, nonce, nc, cnonce, qop, ha2)
				.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Calculates the hashed username value if the authenticate challenge has 'userhash=true'.
	 */
	private String calculateUserhash(Function<byte[], byte[]> digestFunction, String realm) {
		return hexStringOf(digestFunction.apply(String.format("%s:%s", username, realm)
				.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Attempts to retrieve the digest function for the specified algorithm.
	 */
	private static Function<byte[], byte[]> getDigestFunction(String algorithm) {
		if (algorithm.endsWith(SESS)) {
			algorithm = algorithm.substring(0, algorithm.length() - SESS.length());
		}

		try {
			/*
			 * The SHA-512-256 algorithm is the first half of SHA-512 and needs special handling compared to SHA-256
			 * and MD5.
			 */
			if (SHA_512_256.equals(algorithm)) {
				MessageDigest digest = MessageDigest.getInstance("SHA-512");
				return bytes -> Arrays.copyOf(digest.digest(bytes), 32);
			} else {
				MessageDigest digest = MessageDigest.getInstance(algorithm);
				return digest::digest;
			}
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	/**
	 * Splits the Authenticate challenges by the algorithm it uses.
	 */
	private static Map<String, List<Map<String, String>>> partitionByChallengeType(
			List<Map<String, String>> challenges) {
		return challenges.stream().collect(Collectors.groupingBy(headers -> {
			String algorithmHeader = headers.get(ALGORITHM);

			// RFC7616 specifies that is the "algorithm" header is null it defaults to MD5.
			return (algorithmHeader == null) ? MD5 : algorithmHeader.toUpperCase(Locale.ROOT);
		}));
	}

	/**
	 * Creates a unique and secure nonce.
	 */
	String generateNonce() {
		byte[] nonce = new byte[16];
		new SecureRandom().nextBytes(nonce);
		return hexStringOf(nonce);
	}

	/**
	 * Creates the Authorization/Proxy-Authorization header value based on the computed Digest authentication value.
	 */
	private static String buildAuthorizationHeader(String username, String realm, String uri, String algorithm,
			String nonce, int nc, String cnonce, String qop, String response, String opaque, boolean userhash) {
		StringBuilder authorizationBuilder = new StringBuilder(DIGEST);

		authorizationBuilder.append("username=\"").append(username).append("\", ")
				.append("realm=\"").append(realm).append("\", ")
				.append("nonce=\"").append(nonce).append("\", ")
				.append("uri=\"").append(uri).append("\", ")
				.append("response=\"").append(response).append("\"");

		if (!StringUtils.isNullOrEmpty(algorithm)) {
			authorizationBuilder.append(", ").append("algorithm=").append(algorithm);
		}
		if (!StringUtils.isNullOrEmpty(cnonce)) {
			authorizationBuilder.append(", ").append("cnonce=\"").append(cnonce).append("\"");
		}
		if (!StringUtils.isNullOrEmpty(opaque)) {
			authorizationBuilder.append(", ").append("opaque=\"").append(opaque).append("\"");
		}
		if (!StringUtils.isNullOrEmpty(qop)) {
			authorizationBuilder.append(", ").append("qop=").append(qop);
			authorizationBuilder.append(", ").append("nc=").append(String.format("%08X", nc));
		}
		if (userhash) {
			authorizationBuilder.append(", ").append("userhash=").append(true);
		}
		return authorizationBuilder.toString();
	}

	/**
	 * Converts the passed byte array into a hex string.
	 */
	private static String hexStringOf(byte[] bytes) {
		// Hex uses 4 bits, converting a byte to hex will double its size.
		char[] hexCharacters = new char[bytes.length * 2];

		for (int i = 0; i < bytes.length; i++) {
			// Convert the byte into an integer, masking all but the last 8 bits (the byte).
			int b = bytes[i] & 0xFF;

			// Shift 4 times to the right to get the leading 4 bits and get the corresponding hex character.
			hexCharacters[i * 2] = HEX_CHARACTERS[b >>> 4];

			// Mask all but the last 4 bits and get the corresponding hex character.
			hexCharacters[i * 2 + 1] = HEX_CHARACTERS[b & 0x0F];
		}
		return new String(hexCharacters);
	}
}

