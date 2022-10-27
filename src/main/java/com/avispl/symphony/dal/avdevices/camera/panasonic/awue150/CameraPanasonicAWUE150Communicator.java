package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.AuthorizationChallengeHandler;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceInfoMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceURL;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DevicesMetricGroup;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto.SystemInfo;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.ErrorInformation;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.ErrorStatusInformation;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.Fan1Status;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.Fan2Status;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OperationLock;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OutputFormatMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.PowerStatus;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * PanasonicCameraAWUE150Communicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 11/13/2022
 * @since 1.0.0
 */
public class CameraPanasonicAWUE150Communicator extends RestCommunicator implements Monitorable, Controller {

	private String authorizationHeader;
	private Map<String, String> failedMonitor;

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Getting statistics from the device Panasonic camera UE150 at host %s with port %s", this.host, this.getPort()));
		}
		final ExtendedStatistics extendedStatistics = new ExtendedStatistics();
		final Map<String, String> stats = new HashMap<>();
		login();
		retrieveSystemInfo(stats);
		retrieveLiveCameraInfo(stats);
		extendedStatistics.setStatistics(stats);

		return Collections.singletonList(extendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
// ToDo:
	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
// ToDo:
	}

	@Override
	protected void authenticate() throws Exception {
// The device has its own authentication behavior, do not use the common one
	}

	/**
	 * @return HttpHeaders contain cookie for authorization
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		headers.set("Content-Type", "text/xml");
		headers.set("Content-Type", "application/json");

		if (StringUtils.isNotNullOrEmpty(authorizationHeader)) {
			headers.set(AuthorizationChallengeHandler.AUTHORIZATION, authorizationHeader);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * @param path url of the request
	 * @return String full path of the device
	 */
	private String buildDeviceFullPath(String path) {
		Objects.requireNonNull(path);

		return DeviceConstant.HTTPS
				+ getHost()
				+ path;
	}

	/**
	 * this method is used to log in to the camera including the Basic and Digest authentication methods
	 */
	private void login() {
		try {
			HttpClient httpClient = this.obtainHttpClient(true);
			HttpGet httpGet = new HttpGet(buildDeviceFullPath(DeviceURL.FIRST_LOGIN));
			httpGet.setHeader("Content-Type", "text/xml");
			httpGet.setHeader("Content-Type", "application/json");
			HttpResponse response = null;

			try {
				response = httpClient.execute(httpGet);
			} finally {
				if (response instanceof CloseableHttpResponse) {
					((CloseableHttpResponse) response).close();
				}
			}
			String headerResponse = response.getFirstHeader(AuthorizationChallengeHandler.WWW_AUTHENTICATE).toString();

			if (response.getStatusLine().getStatusCode() == HttpStatus.UNAUTHORIZED.value() && StringUtils.isNotNullOrEmpty(headerResponse)) {
				AuthorizationChallengeHandler authorizationChallengeHandler = new AuthorizationChallengeHandler(getLogin(), getPassword());
				List<Map<String, String>> challenges = new ArrayList<>();
				Map<String, String> challenge = authorizationChallengeHandler.parseAuthenticationOrAuthorizationHeader(headerResponse);
				challenges.add(challenge);

				if (headerResponse.contains(DeviceConstant.BASIC)) {
					authorizationHeader = authorizationChallengeHandler.handleBasic();
				} else if (headerResponse.contains(DeviceConstant.DIGEST)) {
					authorizationHeader = authorizationChallengeHandler.handleDigest(HttpMethod.GET.toString(), DeviceURL.FIRST_LOGIN, challenges, null);
				}
			}
		} catch (Exception e) {
			throw new ResourceNotReachableException("Login failed, Please check the username and password", e);
		}
	}

	/**
	 * This method is used to retrieve system information by send get request to "http://***REMOVED***/cgi-bin/getinfo?FILE=1"
	 *
	 * @param stats When there is no response data, the failedMonitor is going to update
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveSystemInfo(Map<String, String> stats) {
		try {
			String request = buildDeviceFullPath(DeviceURL.SYSTEM_INFO);
			String response = doGet(request);

			if (StringUtils.isNotNullOrEmpty(response)) {
				// map data to dto
				String[] fields = response.split(DeviceConstant.REGEX_TRAILING_OF_FIELD);
				Map<String, String> rawObject = new HashMap<>();
				for (String field : fields) {
					String[] fieldElement = field.split(DeviceConstant.EQUAL, 2);
					if (fieldElement.length == 2) {
						rawObject.put(fieldElement[0], fieldElement[1]);
					}
				}

				ObjectMapper objectMapper = new ObjectMapper();
				SystemInfo systemInfo = objectMapper.convertValue(rawObject, SystemInfo.class);
				if (systemInfo != null) {
					stats.put(DeviceInfoMetric.MAC_ADDRESS.getName(), systemInfo.getMacAddress());
					stats.put(DeviceInfoMetric.SERIAL_NUMBER.getName(), systemInfo.getSerialNumber());
					stats.put(DeviceInfoMetric.FIRMWARE_VERSION.getName(), systemInfo.getFirmwareVersion());
					stats.put(DeviceInfoMetric.MODEL_NAME.getName(), systemInfo.getModelName());
				} else {
					updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
				}
			} else {
				updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
			}
		} catch (Exception e) {
			updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), String.format("error while getting system information: %s", e.getMessage()));
		}
	}

	/**
	 * This method is used to retrieve system information by send get request to "http://***REMOVED***/cgi-bin/getinfo?FILE=1"
	 *
	 * @param stats When there is no response data, the failedMonitor is going to update
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveLiveCameraInfo(Map<String, String> stats) {
		try {
			String request = buildDeviceFullPath(DeviceURL.LIVE_CAMERA_DATA);
			String response = doGet(request);

			if (StringUtils.isNotNullOrEmpty(response)) {
				// map data to dto
				String[] splitStrings = response.split(DeviceConstant.REGEX_TRAILING_OF_FIELD);
				Map<String, String> fields = new HashMap<>();
				for (String fieldElement :
						splitStrings) {
					if (fieldElement.startsWith("OSJ:40:")) {
						fields.put(fieldElement.substring(0, 8), fieldElement.substring(8));
						continue;
					}
					if (fieldElement.startsWith("OSJ:5C:")) {
						fields.put(fieldElement.substring(0, 7), fieldElement.substring(7));
						continue;
					}
					fields.put(fieldElement, DeviceConstant.NONE);
				}
				stats.put(DeviceInfoMetric.OUTPUT_FORMAT.getName(), OutputFormatMetric.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.OPERATION_LOCK.getName(), OperationLock.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.CAMERA_TITLE.getName(), getDefaultValueForNullData(convertHexToString(fields.get("OSJ:5C:")), DeviceConstant.NONE));
				stats.put(DeviceInfoMetric.FAN1_STATUS.getName(), Fan1Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.FAN2_STATUS.getName(), Fan2Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_INFORMATION.getName(), ErrorInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_STATUS_INFO.getName(), ErrorStatusInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.POWER_STATUS.getName(), PowerStatus.getByAPIValue(fields).getUiName());
			} else {
				updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), "error while getting camera live information");
			}
		} catch (Exception e) {
			updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), String.format("error while getting camera live information: %s", e.getMessage()));
		}
	}

	/**
	 * This method is used to convert from hex to string
	 *
	 * @param hexString string in hex value
	 * @return String converted string
	 */
	private String convertHexToString(String hexString) {
		try {
			byte[] bytes = Hex.decodeHex(hexString.toCharArray());
			return new String(bytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			logger.error(String.format("error while converting from hex to string: %s", e.getMessage()), e);
		}
		return DeviceConstant.EMPTY;
	}

	/**
	 * get default value for null data
	 *
	 * @param value value of monitoring properties
	 * @return String (none/value)
	 */
	private String getDefaultValueForNullData(String value, String defaultValue) {
		return StringUtils.isNullOrEmpty(value) ? defaultValue : value;
	}

	/**
	 * Update failedMonitor with getting device info error message
	 *
	 * @param monitoringGroup is monitoring metric group
	 * @param errorMessage is error message
	 */
	private void updateFailedMonitor(String monitoringGroup, String errorMessage) {
		failedMonitor.put(monitoringGroup, errorMessage);
	}
}
