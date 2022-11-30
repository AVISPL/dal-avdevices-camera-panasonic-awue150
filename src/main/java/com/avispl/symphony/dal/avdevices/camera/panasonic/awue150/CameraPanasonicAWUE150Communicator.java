package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150;

import static com.avispl.symphony.dal.util.ControllablePropertyFactory.createDropdown;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.util.EntityUtils;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.AuthorizationChallengeHandler;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.Command;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceInfoMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceURL;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DevicesMetricGroup;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DropdownList;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.WebClientConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.AutoFocus;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusADJWithPTZ;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.AWBColorTemperature;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.AWBMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.Gain;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.ImageAdjustControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.IrisMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.NightDayFilter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.NightDayMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.Shutter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.SuperGain;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PanTiltControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PowerOnPosition;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.preset.PresetControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.zoom.ZoomControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto.LiveCameraInfo;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto.SystemInfo;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.ErrorInformation;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.ErrorStatusInformation;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.Fan1Status;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.Fan2Status;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OperationLock;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OutputFormat;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.PowerStatus;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.UHDCrop;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * PanasonicCameraAWUE150Communicator
 * An implementation of RestCommunicator to provide communication and interaction with Panasonic camera AW-UE150
 * <p>
 * Monitoring:
 * <li>DeviceInformation</li>
 * <p>
 * <p>
 * Controlling:
 * <li>Focus</li>
 * <li>Preset</li>
 * <li>Pan/Tilt</li>
 * <li>Zoom</li>
 * <li>ImageAdjust</li>
 * <p>
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 11/13/2022
 * @since 1.0.0
 */
public class CameraPanasonicAWUE150Communicator extends RestCommunicator implements Monitorable, Controller {

	private Map<String, String> failedMonitor = new HashMap<>();
	private boolean isEmergencyDelivery = false;
	private ExtendedStatistics localExtendedStatistics = new ExtendedStatistics();
	private boolean isPresetEdited;
	private String baseRequestUrl;

	/**
	 * store configManagement adapter properties
	 */
	private String configManagement;

	/**
	 * configManagement in boolean value
	 */
	private boolean isConfigManagement;

	/**
	 * Stored list of presetModes
	 */
	private static List<String> presetModes = new ArrayList<>();

	/**
	 * Stored authorization value of basic/ digest authentication
	 */
	private String authorizationHeader;

	/**
	 * ReentrantLock to prevent null pointer exception to localExtendedStatistics when controlProperty method is called before GetMultipleStatistics method.
	 */
	private final ReentrantLock reentrantLock = new ReentrantLock();

	/**
	 * Store list of current live camera information from device
	 */
	private LiveCameraInfo cachedLiveCameraInfo = new LiveCameraInfo();

	/**
	 * Store current focus control speed
	 */
	private float cachedFocusControlSpeed = DeviceConstant.MAX_FOCUS_SPEED_UI_VALUE;

	/**
	 * Store current zoom control speed
	 */
	private float cachedZoomControlSpeed = DeviceConstant.MAX_ZOOM_SPEED_UI_VALUE;

	/**
	 * Store current pan tilt control speed
	 */
	private float cachedPanTiltControlSpeed = DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE;

	/**
	 * Store current preset control
	 */
	private String cachedPresetControl = DeviceConstant.DEFAULT_PRESET;

	/**
	 * Retrieves {@link #configManagement}
	 *
	 * @return value of {@link #configManagement}
	 */
	public String getConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@link #configManagement} value
	 *
	 * @param configManagement new value of {@link #configManagement}
	 */
	public void setConfigManagement(String configManagement) {
		this.configManagement = configManagement;
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(
					String.format("Getting statistics from the device Panasonic camera UE150 at host %s with port %s", this.host,
							this.getPort()));
		}
		reentrantLock.lock();
		try {

			final ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			final Map<String, String> stats = new HashMap<>();
			final List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
			if (!isEmergencyDelivery) {

				// login for the first time
				if (StringUtils.isNullOrEmpty(authorizationHeader)) {
					login();
				}
				// retrieve monitoring/ controlling data
				retrieveSystemInfo(stats);
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				retrieveModelName(stats);
				retrieveSimultaneous();
				checkFailedMonitor();
				retrievePresetName();
				isValidConfigManagement();

				// populate controllable properties
				if (OperationLock.UNLOCK.equals(cachedLiveCameraInfo.getOperationLock()) && PowerStatus.ON.equals(cachedLiveCameraInfo.getPowerStatus()) && isConfigManagement) {
					populateFocusControls(stats, advancedControllableProperties);
					populatePresetControls(stats, advancedControllableProperties);
					populatePanTiltControls(stats, advancedControllableProperties);
					populateZoomControls(stats, advancedControllableProperties);
					populateImageAdjustControls(stats, advancedControllableProperties);
				}
				extendedStatistics.setStatistics(stats);
				extendedStatistics.setControllableProperties(advancedControllableProperties);
				localExtendedStatistics = extendedStatistics;
			}
			isEmergencyDelivery = false;
		} finally {
			reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		reentrantLock.lock();
		try {
			if (this.localExtendedStatistics == null) {
				return;
			}
			Map<String, String> stats = this.localExtendedStatistics.getStatistics();
			List<AdvancedControllableProperty> advancedControllableProperties = this.localExtendedStatistics.getControllableProperties();

			String property = controllableProperty.getProperty();
			String value = String.valueOf(controllableProperty.getValue());
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("controlProperty property " + property);
				this.logger.debug("controlProperty value " + value);
			}

			if (!property.contains(String.valueOf(DeviceConstant.HASH))) {
				DeviceInfoMetric metric = DeviceInfoMetric.getByName(property);
				switch (metric) {
					case POWER_STATUS:
						powerControl(property, value);
						break;
					case OPERATION_LOCK:
						operationLockControl(property, value);
						break;
					default:
						throw new IllegalStateException(String.format("Control %s is not supported", property));
				}
			} else {
				String[] splitProperty = property.split(String.valueOf(DeviceConstant.HASH));
				if (splitProperty.length == 2) {
					DevicesMetricGroup group = DevicesMetricGroup.getByName(splitProperty[0]);
					switch (group) {
						case FOCUS_CONTROL:
							focusControl(stats, advancedControllableProperties, splitProperty[1], value);
							break;
						case PRESET_CONTROL:
							presetControl(stats, advancedControllableProperties, splitProperty[1], value);
							break;
						case PAN_TILT_PAD_CONTROL:
							panTiltControl(stats, advancedControllableProperties, splitProperty[1], value);
							break;
						case ZOOM_CONTROL:
							zoomControl(stats, advancedControllableProperties, splitProperty[1], value);
							break;
						case IMAGE_ADJUST:
							imageAdjustControl(stats, advancedControllableProperties, splitProperty[1], value);
							break;
						default:
							throw new IllegalStateException(String.format("Control group %s is not supported", splitProperty[0]));
					}
				}
			}
		} finally {
			reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> list) throws Exception {
		if (CollectionUtils.isEmpty(list)) {
			throw new IllegalArgumentException("CameraPanasonicAWUE150Communicator: Controllable properties cannot be null or empty");
		}
		for (ControllableProperty controllableProperty : list) {
			controlProperty(controllableProperty);
		}
	}

	@Override
	protected void authenticate() throws Exception {
		// The device has its own authentication behavior, do not use the common one
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void internalInit() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("Internal init is called.");
		}
		super.internalInit();
	}

	/**
	 * @return HttpHeaders contain digest/ basic authorization header
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		if (StringUtils.isNotNullOrEmpty(authorizationHeader)) {
			headers.set(AuthorizationChallengeHandler.AUTHORIZATION, authorizationHeader);
			headers.set(HttpHeaders.HOST, getHost());
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Get data from uri path
	 *
	 * @param uri the uri is the path get from the configuration properties on the symphony portal
	 * @return String This returns the status code and dataBody if the response get body not null
	 * @throws Exception if getting information from the Uri failed
	 */
	@Override
	public String doGet(String uri) throws Exception {
		HttpClient client = this.obtainHttpClient(StringUtils.isNotNullOrEmpty(authorizationHeader));

		String getUri = this.buildRequestUrl(uri);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Performing a GET operation for " + getUri);
		}

		HttpResponse response = null;
		try {
			RequestBuilder requestBuilder = RequestBuilder.get().setUri(getUri);
			processRequestHeaders(requestBuilder);
			response = client.execute(requestBuilder.build());

			int statusCode = response.getStatusLine().getStatusCode();
			HttpEntity httpEntity = response.getEntity();

			if (!HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
				if (HttpStatus.UNAUTHORIZED.value() == statusCode) {
					throw new FailedLoginException("Failed to login, please check the username and password");
				}
				if (HttpStatus.BAD_REQUEST.value() == statusCode) {
					throw new ResourceNotReachableException("Bad request, please check the uri or parameters");
				}
				if (HttpStatus.REQUEST_TIMEOUT.value() == statusCode) {
					throw new TimeoutException("Request time out");
				}
				throw new CommandFailureException(getHost(), requestBuilder.toString(), response.toString());
			}
			if (httpEntity != null) {
				String responseBody = EntityUtils.toString(httpEntity);
				logger.debug(String.format("<%s> : %s", statusCode, responseBody));
				return responseBody;
			}
		} finally {
			if (response instanceof CloseableHttpResponse) {
				((CloseableHttpResponse) response).close();
			}
		}
		return null;
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
	 * This method is used to log in to the camera including the Basic and Digest authentication methods
	 */
	private void login() {
		try {
			HttpClient httpClient = this.obtainHttpClient(true);
			HttpGet httpGet = new HttpGet(buildDeviceFullPath(DeviceURL.FIRST_LOGIN));
			HttpResponse response = null;

			try {
				response = httpClient.execute(httpGet);
			} finally {
				if (response instanceof CloseableHttpResponse) {
					((CloseableHttpResponse) response).close();
				}
			}

			Header header = response.getFirstHeader(AuthorizationChallengeHandler.WWW_AUTHENTICATE);
			if (header != null) {
				String headerResponse = header.toString();
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
			}
		} catch (HttpHostConnectException e) {
			throw new ResourceNotReachableException(String.format("Error while connecting to %s: %s", host, e.getMessage()), e);
		} catch (Exception e) {
			throw new ResourceNotReachableException("Login failed" + e.getMessage(), e);
		}
	}

	/**
	 * This method is used to retrieve system information by send get request to "http://***REMOVED***/cgi-bin/getinfo?FILE=1"
	 *
	 * @param stats store all statistics
	 * When the response is null or empty, the failedMonitor is going to update and exception is not populated
	 * @throws FailedLoginException when login fails
	 */
	private void retrieveSystemInfo(Map<String, String> stats) throws FailedLoginException {
		String request = buildDeviceFullPath(DeviceURL.SYSTEM_INFO);
		String response = sendCameraMonitoringRequest(request, DevicesMetricGroup.SYSTEM_INFO.getName(), true);
		if (StringUtils.isNotNullOrEmpty(response)) {
			SystemInfo systemInfo = null;

			// map data to dto
			try {
				String[] fields = response.split(DeviceConstant.REGEX_TRAILING_OF_FIELD);
				Map<String, String> rawObject = new HashMap<>();
				for (String field : fields) {
					String[] fieldElement = field.split(DeviceConstant.EQUAL, 2);
					if (fieldElement.length == 2) {
						rawObject.put(fieldElement[0], fieldElement[1]);
					}
				}

				ObjectMapper objectMapper = new ObjectMapper();
				systemInfo = objectMapper.convertValue(rawObject, SystemInfo.class);
			} catch (Exception e) {
				logger.error(String.format("error while deserializing system information: %s", e.getMessage()), e);
				updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while deserializing system information");
			}
			if (systemInfo != null) {
				stats.put(DeviceInfoMetric.MAC_ADDRESS.getName(), getDefaultValueForNullData(systemInfo.getMacAddress(), DeviceConstant.NONE));
				stats.put(DeviceInfoMetric.SERIAL_NUMBER.getName(), getDefaultValueForNullData(systemInfo.getSerialNumber(), DeviceConstant.NONE));
				stats.put(DeviceInfoMetric.FIRMWARE_VERSION.getName(), getDefaultValueForNullData(systemInfo.getFirmwareVersion(), DeviceConstant.NONE));
			} else {
				updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
			}
		} else {
			updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
		}
	}

	/**
	 * This method is used to retrieve live camera information by send get request to "http://***REMOVED***/live/camdata.html"
	 *
	 * @param stats store all statistics
	 * When the response is null or empty, the failedMonitor is going to update and exception is not populated
	 * @throws FailedLoginException when login fails
	 */
	private void retrieveLiveCameraInfo(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) throws FailedLoginException {
		String request = buildDeviceFullPath(DeviceURL.LIVE_CAMERA_DATA);
		String response = sendCameraMonitoringRequest(request, DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), true);

		if (StringUtils.isNotNullOrEmpty(response)) {
			// map data to dto
			Map<String, String> fields = new HashMap<>();
			try {
				String[] splitStrings = response.split(DeviceConstant.REGEX_TRAILING_OF_FIELD);
				for (String fieldElement : splitStrings) {
					if (fieldElement.startsWith(Command.OPERATION_LOCK_RESPONSE)) {
						fields.put(fieldElement.substring(0, Command.OPERATION_LOCK_FULL_RESPONSE.length()), fieldElement.substring(Command.OPERATION_LOCK_FULL_RESPONSE.length()));
						continue;
					}
					if (fieldElement.startsWith(Command.FOCUS_POSITION_RESPONSE)) {
						cachedLiveCameraInfo.setFocusPosition(fieldElement.substring(Command.FOCUS_POSITION_RESPONSE.length()));
						continue;
					}
					if (fieldElement.startsWith(Command.TITLE_RESPONSE)) {
						fields.put(Command.TITLE_RESPONSE, fieldElement.substring(Command.TITLE_RESPONSE.length()));
						continue;
					}
					if (fieldElement.startsWith(Command.PRESET_RESPONSE)) {
						cachedLiveCameraInfo.setLastPreset(fieldElement.substring(Command.PRESET_RESPONSE.length()));
						continue;
					}
					if (fieldElement.startsWith(Command.PAN_TILT_POSITION_RESPONSE)) {
						cachedLiveCameraInfo.setPanTiltPosition(fieldElement.substring(Command.PAN_TILT_POSITION_RESPONSE.length()));
						continue;
					}
					if (fieldElement.startsWith(Command.PRESET_00_RESPONSE)) {
						cachedLiveCameraInfo.setPreset01To40(convertHexToBinary(fieldElement.substring(Command.PRESET_00_RESPONSE.length()), 40));
						continue;
					}
					if (fieldElement.startsWith(Command.PRESET_01_RESPONSE)) {
						cachedLiveCameraInfo.setPreset41To80(convertHexToBinary(fieldElement.substring(Command.PRESET_01_RESPONSE.length()), 40));
						continue;
					}
					if (fieldElement.startsWith(Command.PRESET_02_RESPONSE)) {
						cachedLiveCameraInfo.setPreset81To100(convertHexToBinary(fieldElement.substring(Command.PRESET_02_RESPONSE.length()), 20));
						continue;
					}
					if (fieldElement.startsWith(Command.ZOOM_POSITION_RESPONSE)) {
						cachedLiveCameraInfo.setZoomPosition(fieldElement.substring(Command.ZOOM_POSITION_RESPONSE.length()));
					}
					if (fieldElement.startsWith(Command.IRIS_POSITION_RESPONSE)) {
						cachedLiveCameraInfo.setIrisPosition(fieldElement.substring(Command.IRIS_POSITION_RESPONSE.length()));
					}
					if (fieldElement.startsWith(Command.AWB_B_GAIN)) {
						cachedLiveCameraInfo.setAwbBGain(fieldElement.substring(Command.AWB_B_GAIN.length()));
					}
					if (fieldElement.startsWith(Command.AWB_G_GAIN)) {
						cachedLiveCameraInfo.setAwbGGain(fieldElement.substring(Command.AWB_G_GAIN.length()));
					}
					if (fieldElement.startsWith(Command.AWB_R_GAIN)) {
						cachedLiveCameraInfo.setAwbRGain(fieldElement.substring(Command.AWB_R_GAIN.length()));
					}
					if (fieldElement.startsWith(Command.COLOR_TEMPERATURE)) {
						cachedLiveCameraInfo.setColorTemperature(fieldElement.substring(Command.COLOR_TEMPERATURE.length()));
					}
					if (fieldElement.startsWith(Command.PAN_TILT_UP_LIMITATION)) {
						cachedLiveCameraInfo.setPanTiltUpLimitation(fieldElement.substring(Command.PAN_TILT_UP_LIMITATION.length()));
					}
					if (fieldElement.startsWith(Command.PAN_TILT_DOWN_LIMITATION)) {
						cachedLiveCameraInfo.setPanTiltDownLimitation(fieldElement.substring(Command.PAN_TILT_DOWN_LIMITATION.length()));
					}
					if (fieldElement.startsWith(Command.PAN_TILT_LEFT_LIMITATION)) {
						cachedLiveCameraInfo.setPanTiltLeftLimitation(fieldElement.substring(Command.PAN_TILT_LEFT_LIMITATION.length()));
					}
					if (fieldElement.startsWith(Command.PAN_TILT_RIGHT_LIMITATION)) {
						cachedLiveCameraInfo.setPanTiltRightLimitation(fieldElement.substring(Command.PAN_TILT_RIGHT_LIMITATION.length()));
					}
					fields.put(fieldElement, DeviceConstant.NONE);
				}
			} catch (Exception e) {
				logger.error(String.format("error while deserializing live camera information: %s", e.getMessage()), e);
				updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), "error while deserializing system information");
			}

			if (!fields.isEmpty()) {
				// put live camera info to cache
				cachedLiveCameraInfo.setSuperGain(SuperGain.getByAPIValue(fields));
				cachedLiveCameraInfo.setGain(Gain.getByAPIValue(fields));
				cachedLiveCameraInfo.setAwbMode(AWBMode.getByAPINameFirst(fields));
				cachedLiveCameraInfo.setShutter(Shutter.getByAPINameFirst(fields));
				cachedLiveCameraInfo.setNighDayFilter(NightDayFilter.getByAPIValue(fields));
				cachedLiveCameraInfo.setAutoFocus(AutoFocus.getByAPIValue(fields));
				cachedLiveCameraInfo.setFocusADJWithPTZ(FocusADJWithPTZ.getByAPIValue(fields));
				cachedLiveCameraInfo.setOperationLock(OperationLock.getByAPIValue(fields));
				cachedLiveCameraInfo.setPowerStatus(PowerStatus.getByAPINameFirst(fields));
				cachedLiveCameraInfo.setPowerOnPosition(PowerOnPosition.getByAPIValue(fields));
				cachedLiveCameraInfo.setOutputFormat(OutputFormat.getByAPIValue(fields));
				cachedLiveCameraInfo.setIrisMode(IrisMode.getByAPIValue(fields));
				cachedLiveCameraInfo.setNightDayMode(NightDayMode.getByAPIValue(fields));
				cachedLiveCameraInfo.setPresetEntitiesStatus(
						mapPresetEntityToListPreset(cachedLiveCameraInfo.getPreset01To40(), cachedLiveCameraInfo.getPreset41To80(), cachedLiveCameraInfo.getPreset81To100()));

				// populate live camera info
				stats.put(DeviceInfoMetric.OUTPUT_FORMAT.getName(), cachedLiveCameraInfo.getOutputFormat().getUiName());
				stats.put(DeviceInfoMetric.OPERATION_LOCK.getName(), cachedLiveCameraInfo.getOperationLock().getUiName());
				stats.put(DeviceInfoMetric.CAMERA_TITLE.getName(), getDefaultValueForNullData(fields.get(Command.TITLE_RESPONSE), DeviceConstant.NONE));
				stats.put(DeviceInfoMetric.FAN1_STATUS.getName(), Fan1Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.FAN2_STATUS.getName(), Fan2Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_INFORMATION.getName(), ErrorInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_STATUS_INFO.getName(), ErrorStatusInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.POWER_STATUS.getName(), cachedLiveCameraInfo.getPowerStatus().getUiName());
				stats.put(DeviceInfoMetric.UHD_CROP.getName(), UHDCrop.getByAPIValue(fields).getUiName());

				// populate power on/of controllable property
				addAdvanceControlProperties(advancedControllableProperties, stats,
						createSwitch(DeviceInfoMetric.POWER_STATUS.getName(), cachedLiveCameraInfo.getPowerStatus().getCode(), DeviceConstant.STANDBY, DeviceConstant.ON));
				addAdvanceControlProperties(advancedControllableProperties, stats,
						createSwitch(DeviceInfoMetric.OPERATION_LOCK.getName(), cachedLiveCameraInfo.getOperationLock().getCode(), DeviceConstant.UNLOCK, DeviceConstant.LOCK));

				// hidden Power control when operation lock is lock
				if (OperationLock.LOCK.equals(cachedLiveCameraInfo.getOperationLock())) {
					Set<String> unusedKeys = new HashSet<>();
					unusedKeys.add(DeviceInfoMetric.POWER_STATUS.getName());
					removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
				}

			}
		} else {
			updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), "error while getting camera live information");
		}
	}

	/**
	 * This method is used to retrieve model name information by send get request to "http://***REMOVED***/cgi-bin/model_serial"
	 *
	 * @param stats store all statistics
	 * When the response is null or empty, the failedMonitor is going to update and exception is not populated
	 * @throws FailedLoginException when login fails
	 */
	private void retrieveModelName(Map<String, String> stats) throws FailedLoginException {
		String request = buildDeviceFullPath(DeviceURL.MODEL_SERIAL);
		String response = sendCameraMonitoringRequest(request, DevicesMetricGroup.MODEL_NAME.getName(), true);

		if (StringUtils.isNotNullOrEmpty(response) && response.contains(DeviceConstant.COLON)) {
			String[] splitResponse = response.split(DeviceConstant.COLON);
			stats.put(DeviceInfoMetric.MODEL_NAME.getName(), splitResponse[0]);
		} else {
			updateFailedMonitor(DevicesMetricGroup.MODEL_NAME.getName(), "error while getting model name");
		}
	}

	/**
	 * This method is used to retrieve model name information by send get request to "http://***REMOVED***//cgi-bin/aw_ptz?cmd=%23PTD&res=1"
	 * When the response is null or empty, the failedMonitor is going to update and exception is not populated
	 *
	 * @throws FailedLoginException when login fails
	 */
	private void retrieveSimultaneous() throws FailedLoginException {
		String command = buildDeviceFullPath(DeviceURL.CAMERA_PTZ_CONTROL
				.concat(Command.HASH.concat(Command.SIMULTANEOUS))
				.concat(DeviceURL.CAMERA_CONTROL_RES));
		String response = sendCameraMonitoringRequest(command, DevicesMetricGroup.SIMULTANEOUS.getName(), true);
		int panUIValue = (int) DeviceConstant.MIN_PAN_POSITION_UI;
		int tiltUIValue = (int) DeviceConstant.MIN_TILT_POSITION_UI;
		int zoomUIValue = (int) DeviceConstant.MIN_ZOOM_UI_VALUE;
		int focusUIValue = (int) DeviceConstant.MIN_FOCUS_UI_VALUE;
		float irisUIValue = DeviceConstant.MIN_IRIS_UI_VALUE;
		if (StringUtils.isNotNullOrEmpty(response)) {
			try {
				// data format PTDffff0000fff00
				if (response.length() >= 16) {
					String data = response.substring(Command.SIMULTANEOUS.length());
					panUIValue = Integer.parseInt(data.substring(DeviceConstant.PAN_START_INDEX_IN_SIMULTANEOUS_RESPONSE, DeviceConstant.PAN_END_INDEX_IN_SIMULTANEOUS_RESPONSE), 16);
					tiltUIValue = Integer.parseInt(data.substring(DeviceConstant.PAN_END_INDEX_IN_SIMULTANEOUS_RESPONSE, DeviceConstant.TILT_END_INDEX_IN_SIMULTANEOUS_RESPONSE), 16);
					zoomUIValue = Integer.parseInt(data.substring(DeviceConstant.TILT_END_INDEX_IN_SIMULTANEOUS_RESPONSE, DeviceConstant.ZOOM_END_INDEX_IN_SIMULTANEOUS_RESPONSE), 16);
					focusUIValue = Integer.parseInt(data.substring(DeviceConstant.ZOOM_END_INDEX_IN_SIMULTANEOUS_RESPONSE, DeviceConstant.FOCUS_END_INDEX_IN_SIMULTANEOUS_RESPONSE), 16);
					irisUIValue = Integer.parseInt(data.substring(DeviceConstant.FOCUS_END_INDEX_IN_SIMULTANEOUS_RESPONSE, DeviceConstant.IRIS_END_INDEX_IN_SIMULTANEOUS_RESPONSE), 16) / 10f;

					if (zoomUIValue > DeviceConstant.MAX_ZOOM_UI_VALUE) {
						zoomUIValue = (int) DeviceConstant.MAX_ZOOM_UI_VALUE;
					}
					if (zoomUIValue < DeviceConstant.MIN_ZOOM_UI_VALUE) {
						zoomUIValue = (int) DeviceConstant.MIN_ZOOM_UI_VALUE;
					}

					if (focusUIValue > DeviceConstant.MAX_FOCUS_UI_VALUE) {
						focusUIValue = (int) DeviceConstant.MAX_FOCUS_UI_VALUE;
					}
					if (focusUIValue < DeviceConstant.MIN_FOCUS_UI_VALUE) {
						focusUIValue = (int) DeviceConstant.MIN_FOCUS_UI_VALUE;
					}

					if (irisUIValue > DeviceConstant.MAX_IRIS_UI_VALUE) {
						irisUIValue = DeviceConstant.MAX_IRIS_UI_VALUE;
					}
					if (irisUIValue < DeviceConstant.MIN_IRIS_UI_VALUE) {
						irisUIValue = DeviceConstant.MIN_IRIS_UI_VALUE;
					}
				}
			} catch (Exception e) {
				logger.error(String.format("error while deserializing simultaneous information: %s", e.getMessage()), e);
				updateFailedMonitor(DevicesMetricGroup.SIMULTANEOUS.getName(), "error while deserializing system information");
			}
		} else {
			updateFailedMonitor(DevicesMetricGroup.SIMULTANEOUS.getName(), String.format("error while receive %s", DevicesMetricGroup.SIMULTANEOUS.getName()));
		}
		cachedLiveCameraInfo.setPanUIValue(panUIValue);
		cachedLiveCameraInfo.setTiltUIValue(tiltUIValue);
		cachedLiveCameraInfo.setZoomUIValue(zoomUIValue);
		cachedLiveCameraInfo.setFocusUIValue(focusUIValue);
		cachedLiveCameraInfo.setIrisUIValue(irisUIValue);
	}

	/**
	 * This method is used to retrieve model name information by send get request to "http://***REMOVED***//cgi-bin/aw_ptz?cmd=%23PTD&res=1"
	 * When the response is null or empty, the failedMonitor is going to update and exception is not populated
	 *
	 * @throws FailedLoginException when login fails
	 */
	private void retrievePresetName() throws FailedLoginException {
		// get first five preset name
		List<String> presetNames = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			String presetIndex = Command.QUERY_PRESET_NAME.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, i));
			String command = buildDeviceFullPath(DeviceURL.CAMERA_CONTROL
					.concat(presetIndex)
					.concat(DeviceURL.CAMERA_CONTROL_RES));
			String response = sendCameraMonitoringRequest(command, DevicesMetricGroup.SIMULTANEOUS.getName(), true);
			String presetName;
			int ordinal = i + DeviceConstant.INDEX_TO_ORDINAL_CONVERT_FACTOR;
			if (StringUtils.isNullOrEmpty(response) || response.trim().length() < (presetIndex + DeviceConstant.COLON).length()) {
				presetName = String.format("%s %s%s", ordinal, DeviceConstant.PRESET, String.format(DeviceConstant.THREE_NUMBER_FORMAT, ordinal));
			} else {
				presetName = String.format("%s %s", ordinal, response.substring((presetIndex + DeviceConstant.COLON).length()).trim());
			}
			presetNames.add(presetName);
		}
		cachedLiveCameraInfo.setPresetNames(presetNames);
	}

	/**
	 * This method is used to send monitoring request
	 *
	 * @param request monitoring request
	 * @param monitoringGroup monitoring group
	 * @param retryOnUnAuthorized retry on unauthorized request
	 * @return String response
	 * @throws FailedLoginException when log in failed
	 * When exception occur (except FailedLoginException), the failedMonitor is going to update and exception is not populated
	 */
	private String sendCameraMonitoringRequest(String request, String monitoringGroup, boolean retryOnUnAuthorized) throws FailedLoginException {
		try {
			String response = doGet(request);
			if (StringUtils.isNotNullOrEmpty(response)) {
				return response;
			} else {
				updateFailedMonitor(monitoringGroup, String.format("error while receive %s", monitoringGroup));
			}
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				sendCameraMonitoringRequest(request, monitoringGroup, false);
			} else {
				throw new FailedLoginException("Failed to login, please check the username and password");
			}
		} catch (Exception e) {
			logger.error(String.format("error while receive %s: %s", monitoringGroup, e.getMessage()));
			updateFailedMonitor(monitoringGroup, String.format("error while receive %s: %s", monitoringGroup, e.getMessage()));
		}
		return DeviceConstant.EMPTY;
	}

	/**
	 * This method is used to check failed monitoring
	 *
	 * @throws ResourceNotReachableException When all monitoring requests are failed
	 */
	private void checkFailedMonitor() {
		if (failedMonitor.size() >= DeviceConstant.MAX_FAILED_REQUEST) {
			StringBuilder errBuilder = new StringBuilder();
			for (Map.Entry<String, String> failedMetric : failedMonitor.entrySet()) {
				if (failedMetric.getValue().toUpperCase().contains(DeviceConstant.CONNECTION_TIME_OUT)) {
					throw new ResourceNotReachableException("Connection time out");
				}
				errBuilder.append(failedMetric.getValue());
				errBuilder.append(DeviceConstant.SPACE);
				errBuilder.append(DeviceConstant.NEXT_LINE);
			}
			failedMonitor = new HashMap<>();
			throw new ResourceNotReachableException(errBuilder.toString());
		}
	}

	//region focus control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate focus control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populateFocusControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH;

		String focusControlLabelStart = String.valueOf((int) DeviceConstant.MIN_FOCUS_UI_VALUE);
		String focusControlLabelEnd = String.valueOf((int) DeviceConstant.MAX_FOCUS_UI_VALUE);
		String focusSpeedControlLabelStart = String.valueOf((int) DeviceConstant.MIN_FOCUS_SPEED_UI_VALUE);
		String focusControlName = groupName.concat(FocusControlMetric.FOCUS_CONTROL.getName());
		String focusControlFarName = groupName.concat(FocusControlMetric.FOCUS_CONTROL_FAR.getName());
		String focusControlNearName = groupName.concat(FocusControlMetric.FOCUS_CONTROL_NEAR.getName());
		String focusSpeedControlName = groupName.concat(FocusControlMetric.FOCUS_CONTROL_SPEED.getName());
		String focusSpeedCurrentValueName = groupName.concat(FocusControlMetric.FOCUS_CONTROL_SPEED.getName()).concat(FocusControlMetric.CURRENT_VALUE.getName());
		String focusADJWithPTZName = groupName.concat(FocusControlMetric.FOCUS_ADJ_WITH_PTZ.getName());

		if (cachedLiveCameraInfo.getAutoFocus().equals(AutoFocus.AUTO)) {
			Set<String> unusedKeys = new HashSet<>();
			unusedKeys.add(focusControlName);
			unusedKeys.add(focusSpeedControlName);
			unusedKeys.add(focusSpeedCurrentValueName);
			unusedKeys.add(focusControlFarName);
			unusedKeys.add(focusControlNearName);
			unusedKeys.add(focusADJWithPTZName);
			removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
		} else {
			addAdvanceControlProperties(advancedControllableProperties, stats, createSlider(stats, focusControlName, focusControlLabelStart, focusControlLabelEnd,
					DeviceConstant.MIN_FOCUS_UI_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE, (float) cachedLiveCameraInfo.getFocusUIValue()));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createSlider(stats, focusSpeedControlName, focusSpeedControlLabelStart, focusControlLabelEnd,
							DeviceConstant.MIN_FOCUS_SPEED_UI_VALUE, DeviceConstant.MAX_FOCUS_SPEED_UI_VALUE, cachedFocusControlSpeed));
			stats.put(focusSpeedCurrentValueName, String.valueOf((int) cachedFocusControlSpeed));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createButton(focusControlNearName, FocusControlMetric.NEAR.getName(), DeviceConstant.PUSHING));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createButton(focusControlFarName, FocusControlMetric.FAR.getName(), DeviceConstant.PUSHING));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createSwitch(focusADJWithPTZName, cachedLiveCameraInfo.getFocusADJWithPTZ().getCode(), DeviceConstant.OFF, DeviceConstant.ON));
		}
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createSwitch(groupName.concat(FocusControlMetric.AUTO_FOCUS.getName()), cachedLiveCameraInfo.getAutoFocus().getCode(), DeviceConstant.OFF, DeviceConstant.ON));

		stats.put(groupName.concat(FocusControlMetric.FOCUS_MODE.getName()), cachedLiveCameraInfo.getAutoFocus().getUiName());
		stats.put(groupName.concat(FocusControlMetric.FOCUS_CONTROL.getName()).concat(FocusControlMetric.CURRENT_VALUE.getName()),
				getDefaultValueForNullData(String.valueOf(cachedLiveCameraInfo.getFocusUIValue()), DeviceConstant.NONE));
	}

	/**
	 * This method is used to handle focus control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 */
	private void focusControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws
			FailedLoginException {
		FocusControlMetric focusControlMetric = FocusControlMetric.getByName(controllableProperty);
		isEmergencyDelivery = true;
		switch (focusControlMetric) {
			case FOCUS_CONTROL:
				lockFocusControl(controllableProperty);

				String currentValueInHex = convertFromUIValueToAPIValue(Float.parseFloat(value), DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE,
						DeviceConstant.MIN_FOCUS_UI_VALUE);

				String command = Command.HASH.concat(Command.FOCUS).concat(currentValueInHex.toUpperCase());
				String responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_FAR:
				lockFocusControl(controllableProperty);
				Float currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getFocusPosition(),
						DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.FOCUS_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_FOCUS_UI_VALUE) + cachedFocusControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_FOCUS_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_FOCUS_UI_VALUE;
				}

				currentValueInHex = convertFromUIValueToAPIValue(currentUIValue, DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE,
						DeviceConstant.MIN_FOCUS_UI_VALUE);
				command = Command.HASH.concat(Command.FOCUS).concat(currentValueInHex.toUpperCase());
				responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_NEAR:
				lockFocusControl(controllableProperty);
				currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getFocusPosition(), DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE,
						DeviceConstant.FOCUS_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_FOCUS_UI_VALUE) - cachedFocusControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_FOCUS_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_FOCUS_UI_VALUE;
				}
				currentValueInHex = convertFromUIValueToAPIValue(currentUIValue, DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE,
						DeviceConstant.MIN_FOCUS_UI_VALUE);

				command = Command.HASH.concat(Command.FOCUS).concat(currentValueInHex.toUpperCase());
				responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_SPEED:
				cachedFocusControlSpeed = (float) Math.ceil(Float.parseFloat(value));
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case AUTO_FOCUS:
				AutoFocus autoFocus = AutoFocus.getByCode(value);
				String response = performCameraControl(autoFocus.getApiNameSecond(), controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				autoFocus = AutoFocus.getByApiName2(response);
				cachedLiveCameraInfo.setAutoFocus(autoFocus);
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_ADJ_WITH_PTZ:
				FocusADJWithPTZ focusADJWithPTZ = FocusADJWithPTZ.getByAPICode(value);
				response = performCameraControl(focusADJWithPTZ.getApiName(), controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				focusADJWithPTZ = FocusADJWithPTZ.getByAPIValue(response);
				cachedLiveCameraInfo.setFocusADJWithPTZ(focusADJWithPTZ);
				populateFocusControls(stats, advancedControllableProperties);
				break;
			default:
				throw new IllegalStateException(String.format("focus control %s is not supported.", controllableProperty));
		}
	}

	/**
	 * This method is used to lock focus control
	 *
	 * @param controllableProperty controllable property
	 * @throws IllegalStateException when autofocus is enabled
	 */
	private void lockFocusControl(String controllableProperty) {
		if (AutoFocus.AUTO.equals(cachedLiveCameraInfo.getAutoFocus())) {
			throw new IllegalStateException(String.format("Failed to control %s, please change the Focus mode to manual to control %s", controllableProperty, controllableProperty));
		}
	}

	/**
	 * This method is used to convert color temperature form api to UI value
	 *
	 * @param colorTemperature API color temperature value in hex string
	 * @return String UI color temperature value
	 */
	private String convertColorTemperatureFromAPIToUIValue(String colorTemperature) {
		if (colorTemperature.length() >= 6) {
			String splits[] = colorTemperature.split(DeviceConstant.COLON);
			int temperature = Integer.parseInt(splits[0], 16);
			String description = AWBColorTemperature.getByAPIName(splits[1]).getUiName();
			cachedLiveCameraInfo.setColorTemperatureValue(String.valueOf(temperature));
			cachedLiveCameraInfo.setColorTemperatureStatus(description);
			return temperature + DeviceConstant.KELVIN + DeviceConstant.SPACE + description;
		}
		return DeviceConstant.NONE;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region preset control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate preset control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populatePresetControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH;
		if (!isPresetEdited) {
			presetModes.clear();
			presetModes.add(DeviceConstant.DEFAULT_PRESET);
			if (cachedLiveCameraInfo.getPresetNames().size() == DeviceConstant.FIST_FIVE_PRESET_INDEX) {
				presetModes.addAll(cachedLiveCameraInfo.getPresetNames());
				for (int index = DeviceConstant.AFTER_FIST_FIVE_PRESET_INDEX; index <= DeviceConstant.PRESET_INDEX_MAX; index++) {
					presetModes.add(index + DeviceConstant.SPACE + PresetControlMetric.PRESET.getName().concat(String.format(DeviceConstant.THREE_NUMBER_FORMAT, index)));
				}
			} else {
				for (int index = DeviceConstant.PRESET_INDEX_MIN; index <= DeviceConstant.PRESET_INDEX_MAX; index++) {
					presetModes.add(index + DeviceConstant.SPACE + PresetControlMetric.PRESET.getName().concat(String.format(DeviceConstant.THREE_NUMBER_FORMAT, index)));
				}
			}
		}

		// add first five preset name that are gotten from device when all the names are gotten successful
		addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(groupName.concat(PresetControlMetric.PRESET.getName()), presetModes, cachedPresetControl));

		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PresetControlMetric.HOME.getName()), DeviceConstant.HOME, DeviceConstant.PUSHING));
		if (!DeviceConstant.DEFAULT_PRESET.equals(cachedPresetControl)) {
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createButton(groupName.concat(PresetControlMetric.DELETE_PRESET.getName()), DeviceConstant.DELETE, DeviceConstant.DELETING));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createButton(groupName.concat(PresetControlMetric.APPLY_PRESET.getName()), DeviceConstant.APPLY, DeviceConstant.APPLYING));
			addAdvanceControlProperties(advancedControllableProperties, stats,
					createButton(groupName.concat(PresetControlMetric.SET_PRESET.getName()), DeviceConstant.SET, DeviceConstant.SETTING));
		} else {
			Set<String> unusedKeys = new HashSet<>();
			unusedKeys.add(groupName.concat(PresetControlMetric.DELETE_PRESET.getName()));
			unusedKeys.add(groupName.concat(PresetControlMetric.APPLY_PRESET.getName()));
			unusedKeys.add(groupName.concat(PresetControlMetric.SET_PRESET.getName()));
			removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
		}
		stats.put(groupName.concat(PresetControlMetric.LAST_PRESET.getName()),
				presetModes.get(Integer.parseInt(getDefaultValueForNullData(cachedLiveCameraInfo.getLastPreset(), String.valueOf(DeviceConstant.DEFAULT_VALUE)))));
	}

	/**
	 * This method is used to handle preset control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 */
	private void presetControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws
			FailedLoginException {
		PresetControlMetric presetControlMetric = PresetControlMetric.getByName(controllableProperty);
		String presetIndexAPIValue = String.valueOf(DeviceConstant.DEFAULT_PRESET_INDEX);

		if (!DeviceConstant.DEFAULT_PRESET.equals(cachedPresetControl)) {
			Integer index = -1;
			for (int i = DeviceConstant.PRESET_INDEX_MIN; i <= DeviceConstant.PRESET_INDEX_MAX; i++) {
				if (presetModes.get(i).equals(cachedPresetControl)) {
					index = i - DeviceConstant.INDEX_TO_ORDINAL_CONVERT_FACTOR;
					break;
				}
			}
			presetIndexAPIValue = String.format(DeviceConstant.TWO_NUMBER_FORMAT, index);
		}
		switch (presetControlMetric) {
			case PRESET:
				cachedPresetControl = value;
				populatePresetControls(stats, advancedControllableProperties);
				isPresetEdited = true;
				isEmergencyDelivery = true;
				break;
			case SET_PRESET:
				String command = Command.HASH.concat(Command.SET_PRESET.concat(presetIndexAPIValue));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				isPresetEdited = false;
				break;
			case DELETE_PRESET:
				command = Command.HASH.concat(Command.DELETE_PRESET.concat(presetIndexAPIValue));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				isPresetEdited = false;
				break;
			case APPLY_PRESET:
				char presetEntity;
				try {
					presetEntity = cachedLiveCameraInfo.getPresetEntitiesStatus().get(Integer.parseInt(presetIndexAPIValue));
				} catch (Exception e) {
					throw new IllegalStateException(String.format("%s is not exist, please try another preset", cachedPresetControl));
				}
				if (Objects.equals(presetEntity, Command.DELETE_PRESET_SUCCESSFUL_RESPONSE)) {
					throw new IllegalStateException(String.format("%s is not set, please try another preset", cachedPresetControl));
				} else {
					command = Command.HASH.concat(Command.APPLY_PRESET.concat(presetIndexAPIValue));
					String response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
					cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
					cachedLiveCameraInfo.setLastPreset(String.valueOf(Integer.parseInt(response.substring(Command.PRESET_RESPONSE.length())) + 1));

					// camera need 2s to apply new value
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
					}
					isPresetEdited = false;
					break;
				}
			case HOME:
				performCameraControl(Command.HASH.concat(Command.HOME_PRESET), value, controllableProperty, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				populatePresetControls(stats, advancedControllableProperties);
				isEmergencyDelivery = true;
				isPresetEdited = false;
				break;
			default:
				throw new IllegalStateException(String.format("preset control %s is not supported.", controllableProperty));
		}
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region pan/ tilt control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate pan tilt control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populatePanTiltControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH;
		String upLabel = DeviceConstant.UP;
		if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltUpLimitation())) {
			upLabel = DeviceConstant.DISABLED;
		}

		String downLabel = DeviceConstant.DOWN;
		if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltDownLimitation())) {
			downLabel = DeviceConstant.DISABLED;
		}

		String leftLabel = DeviceConstant.LEFT;
		if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltLeftLimitation())) {
			leftLabel = DeviceConstant.DISABLED;
		}

		String rightLabel = DeviceConstant.RIGHT;
		if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltRightLimitation())) {
			rightLabel = DeviceConstant.DISABLED;
		}

		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.HOME.getName()), DeviceConstant.PT_HOME, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.UP.getName()), upLabel, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.DOWN.getName()), downLabel, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.LEFT.getName()), leftLabel, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.RIGHT.getName()), rightLabel, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.UP_LEFT.getName()), DeviceConstant.UP_LEFT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.UP_RIGHT.getName()), DeviceConstant.UP_RIGHT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.DOWN_LEFT.getName()), DeviceConstant.DOWN_LEFT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(PanTiltControlMetric.DOWN_RIGHT.getName()), DeviceConstant.DOWN_RIGHT, DeviceConstant.PUSHING));

		String panTiltSpeedLabelName = groupName.concat(PanTiltControlMetric.PT_SPEED.getName());
		String labelStart = String.valueOf((int) DeviceConstant.MIN_PAN_TILT_SPEED_UI_VALUE);
		String labelEnd = String.valueOf((int) DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE);

		addAdvanceControlProperties(advancedControllableProperties, stats,
				createSlider(stats, panTiltSpeedLabelName, labelStart, labelEnd, DeviceConstant.MIN_PAN_TILT_SPEED_UI_VALUE, DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE, cachedPanTiltControlSpeed));
		stats.put(groupName.concat(PanTiltControlMetric.POWER_ON_POSITION.getName()), cachedLiveCameraInfo.getPowerOnPosition().getUiName());
		stats.put(groupName.concat(PanTiltControlMetric.PT_SPEED_CURRENT_VALUE.getName()), String.valueOf((int) cachedPanTiltControlSpeed));

		convertPanTiltPositionFromApiToUiValue(getDefaultValueForNullData(cachedLiveCameraInfo.getPanTiltPosition(), DeviceConstant.EMPTY));
		stats.put(groupName.concat(PanTiltControlMetric.PAN_ABSOLUTE_POSITION_CONTROL.getName()), cachedLiveCameraInfo.getPanPosition());
		stats.put(groupName.concat(PanTiltControlMetric.TILT_ABSOLUTE_POSITION_CONTROL.getName()), cachedLiveCameraInfo.getTiltPosition());
	}

	/**
	 * This method is used to handle pan tilt control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 */
	private void panTiltControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws
			FailedLoginException {
		PanTiltControlMetric panTiltControlMetric = PanTiltControlMetric.getByName(controllableProperty);
		switch (panTiltControlMetric) {
			case UP:
				lockPanTiltControl(PanTiltControlMetric.UP);

				int currentCommandValue = DeviceConstant.MIN_TILT_UP_API_VALUE + (int) cachedPanTiltControlSpeed;
				if (currentCommandValue > DeviceConstant.MAX_TILT_UP_API_VALUE) {
					currentCommandValue = DeviceConstant.MAX_TILT_UP_API_VALUE;
				}
				String command = Command.PAN_TILT
						.concat(DeviceConstant.PAN_STOP_API_VALUE
								.concat(String.valueOf(currentCommandValue)));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case DOWN:
				lockPanTiltControl(PanTiltControlMetric.DOWN);

				currentCommandValue = DeviceConstant.MAX_TILT_DOWN_API_VALUE - (int) cachedPanTiltControlSpeed;
				if (currentCommandValue < DeviceConstant.MIN_TILT_DOWN_API_VALUE) {
					currentCommandValue = DeviceConstant.MIN_TILT_DOWN_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(DeviceConstant.PAN_STOP_API_VALUE
								.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentCommandValue)));

				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case LEFT:
				lockPanTiltControl(PanTiltControlMetric.LEFT);

				currentCommandValue = DeviceConstant.MAX_PAN_LEFT_API_VALUE - (int) cachedPanTiltControlSpeed;
				if (currentCommandValue < DeviceConstant.MIN_PAN_LEFT_API_VALUE) {
					currentCommandValue = DeviceConstant.MIN_PAN_LEFT_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentCommandValue))
						.concat(DeviceConstant.PAN_STOP_API_VALUE);
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case RIGHT:
				lockPanTiltControl(PanTiltControlMetric.RIGHT);

				currentCommandValue = DeviceConstant.MIN_PAN_RIGHT_API_VALUE + (int) cachedPanTiltControlSpeed;
				if (currentCommandValue > DeviceConstant.MAX_PAN_RIGHT_API_VALUE) {
					currentCommandValue = DeviceConstant.MAX_PAN_RIGHT_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.valueOf(currentCommandValue))
						.concat(DeviceConstant.PAN_STOP_API_VALUE);
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case UP_LEFT:
				int currentPanCommandValue = DeviceConstant.MAX_PAN_LEFT_API_VALUE - (int) cachedPanTiltControlSpeed;
				int currentTiltCommandValue = DeviceConstant.MIN_PAN_RIGHT_API_VALUE + (int) cachedPanTiltControlSpeed;

				if (currentPanCommandValue < DeviceConstant.MIN_PAN_LEFT_API_VALUE) {
					currentPanCommandValue = DeviceConstant.MIN_PAN_LEFT_API_VALUE;
				}
				if (currentTiltCommandValue > DeviceConstant.MAX_TILT_UP_API_VALUE) {
					currentTiltCommandValue = DeviceConstant.MAX_TILT_UP_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentPanCommandValue))
						.concat(String.valueOf(currentTiltCommandValue));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case UP_RIGHT:
				currentCommandValue = DeviceConstant.MIN_PAN_RIGHT_API_VALUE + (int) cachedPanTiltControlSpeed;
				if (currentCommandValue > DeviceConstant.MAX_TILT_UP_API_VALUE) {
					currentCommandValue = DeviceConstant.MAX_TILT_UP_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.valueOf(currentCommandValue))
						.concat(String.valueOf(currentCommandValue));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case DOWN_RIGHT:
				currentPanCommandValue = DeviceConstant.MIN_PAN_RIGHT_API_VALUE + (int) cachedPanTiltControlSpeed;
				currentTiltCommandValue = DeviceConstant.MAX_TILT_DOWN_API_VALUE - (int) cachedPanTiltControlSpeed;

				if (currentPanCommandValue > DeviceConstant.MAX_PAN_RIGHT_API_VALUE) {
					currentPanCommandValue = DeviceConstant.MAX_PAN_RIGHT_API_VALUE;
				}
				if (currentTiltCommandValue < DeviceConstant.MIN_TILT_DOWN_API_VALUE) {
					currentTiltCommandValue = DeviceConstant.MIN_TILT_DOWN_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.valueOf(currentPanCommandValue))
						.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentTiltCommandValue));
				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case DOWN_LEFT:
				currentCommandValue = DeviceConstant.MAX_PAN_LEFT_API_VALUE - (int) cachedPanTiltControlSpeed;
				if (currentCommandValue < DeviceConstant.MIN_PAN_LEFT_API_VALUE) {
					currentCommandValue = DeviceConstant.MIN_PAN_LEFT_API_VALUE;
				}
				command = Command.PAN_TILT
						.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentCommandValue))
						.concat(String.format(DeviceConstant.TWO_NUMBER_FORMAT, currentCommandValue));

				performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 1s to apply new value
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				performCameraControl(Command.PAN_TILT_STOP, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);

				// camera need 2s to apply new value
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			case HOME:
				performCameraControl(Command.HASH.concat(Command.HOME_PRESET), value, controllableProperty, DeviceURL.CAMERA_PTZ_CONTROL, true);
				break;
			case PT_SPEED:
				cachedPanTiltControlSpeed = Float.parseFloat(value);
				populatePanTiltControls(stats, advancedControllableProperties);
				break;
			default:
				throw new IllegalStateException(String.format("preset control %s is not supported.", controllableProperty));
		}
		populatePresetControls(stats, advancedControllableProperties);
	}

	/**
	 * This method is used to convert pan tilt position from API value to UI value
	 *
	 * @param panTiltValue API pan tilt value in hex string
	 * @return String UI pan tilt value
	 */
	private void convertPanTiltPositionFromApiToUiValue(String panTiltValue) {
		if (panTiltValue.length() == DeviceConstant.DEFAULT_PAN_POSITION.length() + DeviceConstant.DEFAULT_TILT_POSITION.length()) {
			String currentPanValue = panTiltValue.substring(0, DeviceConstant.DEFAULT_PAN_POSITION.length());
			String currentTiltValue = panTiltValue.substring(DeviceConstant.DEFAULT_PAN_POSITION.length());
			String panPosition = String.valueOf((int) convertFromApiValueToUIValue(currentPanValue, DeviceConstant.MAX_PAN_POSITION_API_VALUE,
					DeviceConstant.MIN_PAN_POSITION_API_VALUE, DeviceConstant.PAN_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_PAN_POSITION_UI));
			String tiltPosition = String.valueOf((int) convertFromApiValueToUIValue(currentTiltValue, DeviceConstant.MAX_TILT_POSITION_API_VALUE,
					DeviceConstant.MIN_TILT_POSITION_API_VALUE, DeviceConstant.TILT_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_TILT_POSITION_UI));

			cachedLiveCameraInfo.setPanPosition(panPosition);
			cachedLiveCameraInfo.setTiltPosition(tiltPosition);
		}
	}

	/**
	 * This method is used to lock pan tilt control
	 *
	 * @param panTiltControlMetric panTiltControlMetric
	 */
	private void lockPanTiltControl(PanTiltControlMetric panTiltControlMetric) {
		switch (panTiltControlMetric) {
			case UP:
				if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltUpLimitation())) {
					throw new IllegalStateException("The Pan/Tilt up control is disabled, please enable it in the device's setup or try another Pan/Tilt control");
				}
				break;
			case DOWN:
				if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltDownLimitation())) {
					throw new IllegalStateException("The Pan/Tilt down control is disabled, please enable it in the device's setup or try another Pan/Tilt control");
				}
				break;
			case LEFT:
				if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltLeftLimitation())) {
					throw new IllegalStateException("The Pan/Tilt left control is disabled, please enable it in the device's setup or try another Pan/Tilt control");
				}
				break;
			case RIGHT:
				if (Command.DISABLE.equals(cachedLiveCameraInfo.getPanTiltRightLimitation())) {
					throw new IllegalStateException("The Pan/Tilt right control is disabled, please enable it in the device's setup or try another Pan/Tilt control");
				}
				break;
			default:
				logger.debug(String.format("Unsupported pan tilt control: %s", panTiltControlMetric.getName()));
				break;
		}

	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region zoom control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate zoom control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populateZoomControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH;

		String zoomControlLabelStart = String.valueOf((int) DeviceConstant.MIN_ZOOM_UI_VALUE);
		String zoomControlLabelEnd = String.valueOf((int) DeviceConstant.MAX_ZOOM_UI_VALUE);
		String zoomSpeedControlLabelStart = String.valueOf((int) DeviceConstant.MIN_ZOOM_SPEED_UI_VALUE);
		Float currentZoomPosition = convertFromApiValueToUIValue(cachedLiveCameraInfo.getZoomPosition(),
				DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.ZOOM_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_ZOOM_UI_VALUE);

		addAdvanceControlProperties(advancedControllableProperties, stats, createSlider(stats, groupName.concat(ZoomControlMetric.ZOOM_CONTROL.getName()), zoomControlLabelStart, zoomControlLabelEnd,
				DeviceConstant.MIN_ZOOM_UI_VALUE, DeviceConstant.MAX_ZOOM_UI_VALUE, currentZoomPosition));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createSlider(stats, groupName.concat(ZoomControlMetric.ZOOM_CONTROL_SPEED.getName()), zoomSpeedControlLabelStart, zoomControlLabelEnd,
						DeviceConstant.MIN_ZOOM_SPEED_UI_VALUE, DeviceConstant.MAX_ZOOM_UI_VALUE, cachedZoomControlSpeed));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(ZoomControlMetric.ZOOM_OUT.getName()), ZoomControlMetric.WIDE.getName(), DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createButton(groupName.concat(ZoomControlMetric.ZOOM_IN.getName()), ZoomControlMetric.TELE.getName(), DeviceConstant.PUSHING));
		stats.put(groupName.concat(ZoomControlMetric.ZOOM_CONTROL.getName()).concat(ZoomControlMetric.CURRENT_VALUE.getName()),
				getDefaultValueForNullData(String.valueOf(cachedLiveCameraInfo.getZoomUIValue()), DeviceConstant.NONE));
		stats.put(groupName.concat(ZoomControlMetric.ZOOM_CONTROL_SPEED.getName()).concat(ZoomControlMetric.CURRENT_VALUE.getName()), String.valueOf((int) cachedZoomControlSpeed));
	}

	/**
	 * This method is used to handle zoom control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 * @throws FailedLoginException when login fails
	 */
	private void zoomControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws FailedLoginException {
		ZoomControlMetric zoomControlMetric = ZoomControlMetric.getByName(controllableProperty);
		isEmergencyDelivery = true;
		switch (zoomControlMetric) {
			case ZOOM_CONTROL:
				String currentValueInHex = convertFromUIValueToAPIValue(Float.parseFloat(value), DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.MAX_ZOOM_UI_VALUE,
						DeviceConstant.MIN_ZOOM_UI_VALUE);

				String command = Command.HASH.concat(Command.ZOOM.concat(currentValueInHex.toUpperCase()));
				String responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setZoomPosition(responseValueInHex.substring(Command.ZOOM.length()));

				// camera need 3s to apply new value
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				break;
			case ZOOM_IN:
				Float currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getZoomPosition(),
						DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.ZOOM_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_ZOOM_UI_VALUE) + cachedZoomControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_ZOOM_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_ZOOM_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_ZOOM_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_ZOOM_UI_VALUE;
				}

				currentValueInHex = convertFromUIValueToAPIValue(currentUIValue, DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.MAX_ZOOM_UI_VALUE,
						DeviceConstant.MIN_ZOOM_UI_VALUE);
				command = Command.HASH.concat(Command.ZOOM.concat(currentValueInHex.toUpperCase()));
				responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setZoomPosition(responseValueInHex.substring(Command.ZOOM.length()));

				// camera need 3s to apply new value
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				break;
			case ZOOM_OUT:
				currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getZoomPosition(),
						DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.ZOOM_UI_API_CONVERT_FACTOR, DeviceConstant.MIN_ZOOM_UI_VALUE) - cachedZoomControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_ZOOM_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_ZOOM_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_ZOOM_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_ZOOM_UI_VALUE;
				}

				currentValueInHex = convertFromUIValueToAPIValue(currentUIValue, DeviceConstant.MAX_ZOOM_API_VALUE, DeviceConstant.MIN_ZOOM_API_VALUE, DeviceConstant.MAX_ZOOM_UI_VALUE,
						DeviceConstant.MIN_ZOOM_UI_VALUE);
				command = Command.HASH.concat(Command.ZOOM.concat(currentValueInHex.toUpperCase()));
				responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setZoomPosition(responseValueInHex.substring(Command.ZOOM.length()));

				// camera need 3s to apply new value
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				break;
			case ZOOM_CONTROL_SPEED:
				cachedZoomControlSpeed = (float) Math.ceil(Float.parseFloat(value));
				break;
			default:
				throw new IllegalStateException(String.format("zoom control %s is not supported.", controllableProperty));
		}
		populateZoomControls(stats, advancedControllableProperties);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region image adjust control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate image adjust control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populateImageAdjustControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH;
		AWBMode currentWBMode = cachedLiveCameraInfo.getAwbMode();
		Shutter currentShutterMode = cachedLiveCameraInfo.getShutter();
		NightDayFilter currentNightDayFilterMode = cachedLiveCameraInfo.getNighDayFilter();

		List<String> awbModes = DropdownList.getListOfEnumNames(AWBMode.class);
		List<String> shutterModes = DropdownList.getListOfEnumNames(Shutter.class).stream().collect(Collectors.toList());
		List<String> ndFilterModes = DropdownList.getListOfEnumNames(NightDayFilter.class);

		addAdvanceControlProperties(advancedControllableProperties, stats,
				createDropdown(groupName.concat(ImageAdjustControlMetric.AWB.getName()), awbModes, currentWBMode.getUiName()));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createDropdown(groupName.concat(ImageAdjustControlMetric.SHUTTER.getName()), shutterModes, currentShutterMode.getUiName()));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createDropdown(groupName.concat(ImageAdjustControlMetric.ND_FILTER.getName()), ndFilterModes, currentNightDayFilterMode.getUiName()));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createSwitch(groupName.concat(ImageAdjustControlMetric.IRIS_AUTO_TRIGGER.getName()), cachedLiveCameraInfo.getIrisMode().getCode(), DeviceConstant.OFF, DeviceConstant.ON));
		addAdvanceControlProperties(advancedControllableProperties, stats,
				createSwitch(groupName.concat(ImageAdjustControlMetric.NIGHT_DAY_MODE.getName()), cachedLiveCameraInfo.getNightDayMode().getCode(), DeviceConstant.DAY, DeviceConstant.NIGHT));
		populateIrisControl(stats, advancedControllableProperties);
		populateGainControl(stats, advancedControllableProperties);

		float awbRGain = convertFromApiValueToUIValue(cachedLiveCameraInfo.getAwbRGain(), DeviceConstant.MAX_AWB_GAIN_API_VALUE,
				DeviceConstant.MIN_AWB_GAIN_API_VALUE, DeviceConstant.AWB_GAIN_API_UI_VALUE_CONVERT_FACTOR, DeviceConstant.MIN_AWB_GAIN_UI_VALUE);
		float awbGGain = convertFromApiValueToUIValue(cachedLiveCameraInfo.getAwbGGain(), DeviceConstant.MAX_AWB_GAIN_API_VALUE,
				DeviceConstant.MIN_AWB_GAIN_API_VALUE, DeviceConstant.AWB_GAIN_API_UI_VALUE_CONVERT_FACTOR, DeviceConstant.MIN_AWB_GAIN_UI_VALUE);
		float awbBGain = convertFromApiValueToUIValue(cachedLiveCameraInfo.getAwbBGain(), DeviceConstant.MAX_AWB_GAIN_API_VALUE,
				DeviceConstant.MIN_AWB_GAIN_API_VALUE, DeviceConstant.AWB_GAIN_API_UI_VALUE_CONVERT_FACTOR, DeviceConstant.MIN_AWB_GAIN_UI_VALUE);

		stats.put(groupName.concat(ImageAdjustControlMetric.AWB_R_GAIN.getName()), String.valueOf((int) awbRGain));
		stats.put(groupName.concat(ImageAdjustControlMetric.AWB_G_GAIN.getName()), String.valueOf((int) awbGGain));
		stats.put(groupName.concat(ImageAdjustControlMetric.AWB_B_GAIN.getName()), String.valueOf((int) awbBGain));
		convertColorTemperatureFromAPIToUIValue(cachedLiveCameraInfo.getColorTemperature());
		stats.put(groupName.concat(ImageAdjustControlMetric.AWB_COLOR_TEMPERATURE.getName()), cachedLiveCameraInfo.getColorTemperatureValue());
		stats.put(groupName.concat(ImageAdjustControlMetric.AWB_COLOR_TEMPERATURE_STATUS.getName()), cachedLiveCameraInfo.getColorTemperatureStatus());

		// remove unused keys
		Set<String> unusedKeys = new HashSet<>();
		if (cachedLiveCameraInfo.getIrisMode().equals(IrisMode.AUTO)) {
			unusedKeys.add(groupName.concat(ImageAdjustControlMetric.IRIS.getName()));
		}
		if (cachedLiveCameraInfo.getNightDayMode().equals(NightDayMode.NIGHT)) {
			unusedKeys.add(groupName.concat(ImageAdjustControlMetric.ND_FILTER.getName()));
		}
		removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
	}

	/**
	 * This method is used to populate iris control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populateIrisControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH;
		float minIrisUIValue;
		String irisControlLabelEnd;

		String irisControlLabelStart = 'F' + String.valueOf(DeviceConstant.MAX_IRIS_UI_VALUE);
		Float currentIrisPosition = cachedLiveCameraInfo.getIrisUIValue();
		String currentIrisPositionInHex = cachedLiveCameraInfo.getIrisPosition();
		if (currentIrisPositionInHex.equals(DeviceConstant.MAX_IRIS_API_VALUE)) {
			minIrisUIValue = currentIrisPosition;
			cachedLiveCameraInfo.setIrisLimitation(minIrisUIValue);
		}

		if (cachedLiveCameraInfo.getIrisLimitation() == null) {
			minIrisUIValue = DeviceConstant.MIN_IRIS_UI_VALUE;
			irisControlLabelEnd = 'F' + String.valueOf(minIrisUIValue);
		} else {
			minIrisUIValue = cachedLiveCameraInfo.getIrisLimitation();
			irisControlLabelEnd = 'F' + String.valueOf(cachedLiveCameraInfo.getIrisLimitation());
		}

		addAdvanceControlProperties(advancedControllableProperties, stats, createSlider(stats, groupName.concat(ImageAdjustControlMetric.IRIS.getName()), irisControlLabelStart, irisControlLabelEnd,
				minIrisUIValue, DeviceConstant.MAX_IRIS_UI_VALUE, DeviceConstant.MAX_IRIS_UI_VALUE - currentIrisPosition + minIrisUIValue));

		stats.put(groupName.concat(ImageAdjustControlMetric.IRIS.getName()).concat(ImageAdjustControlMetric.CURRENT_VALUE.getName()),
				getDefaultValueForNullData(String.valueOf(currentIrisPosition), DeviceConstant.NONE));
	}

	/**
	 * This method is used to populate gain control
	 *
	 * @param stats store all stats
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populateGainControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		Gain gainCurrentValue = cachedLiveCameraInfo.getGain();
		List<String> gainModes = DropdownList.getListOfEnumNames(Gain.class);

		if (SuperGain.OFF.equals(cachedLiveCameraInfo.getSuperGain())) {
			gainModes.remove(Gain.G_37.getUiName());
			gainModes.remove(Gain.G_38.getUiName());
			gainModes.remove(Gain.G_39.getUiName());
			gainModes.remove(Gain.G_40.getUiName());
			gainModes.remove(Gain.G_41.getUiName());
			gainModes.remove(Gain.G_42.getUiName());
		}
		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.GAIN.getName();
		addAdvanceControlProperties(advancedControllableProperties, stats, createDropdown(propertyName, gainModes, gainCurrentValue.getUiName()));
	}

	/**
	 * This method is used to handle image adjust control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 * @throws FailedLoginException when login fails
	 */
	private void imageAdjustControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws FailedLoginException {
		ImageAdjustControlMetric imageAdjustControlMetric = ImageAdjustControlMetric.getByName(controllableProperty);
		isEmergencyDelivery = true;
		switch (imageAdjustControlMetric) {
			case IRIS:
				if (IrisMode.AUTO.equals(cachedLiveCameraInfo.getIrisMode())) {
					throw new IllegalStateException("Failed to control the iris, please change the iris mode to manual to control the iris ");
				}
				Float currentValueInFloat = Float.parseFloat(value);
				if (currentValueInFloat > DeviceConstant.MAX_IRIS_UI_VALUE) {
					currentValueInFloat = DeviceConstant.MAX_IRIS_UI_VALUE;
				}
				if (currentValueInFloat < DeviceConstant.MIN_IRIS_UI_VALUE) {
					currentValueInFloat = DeviceConstant.MIN_IRIS_UI_VALUE;
				}
				float minIrisUIValue;
				if (cachedLiveCameraInfo.getIrisLimitation() == null) {
					minIrisUIValue = DeviceConstant.MIN_IRIS_UI_VALUE;
				} else {
					minIrisUIValue = cachedLiveCameraInfo.getIrisLimitation();
				}
				String currentValueInHex = convertFromUIValueToAPIValue(currentValueInFloat, DeviceConstant.MAX_IRIS_API_VALUE, DeviceConstant.MIN_IRIS_API_VALUE,
						DeviceConstant.MAX_IRIS_UI_VALUE - minIrisUIValue, DeviceConstant.MIN_IRIS_UI_VALUE);
				String command = Command.HASH.concat(Command.IRIS).concat(currentValueInHex.toUpperCase());
				String responseValueInHex = performCameraControl(command, controllableProperty,
						String.valueOf(DeviceConstant.MAX_IRIS_UI_VALUE - currentValueInFloat + DeviceConstant.MIN_IRIS_UI_VALUE), DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setIrisPosition(responseValueInHex.substring(Command.IRIS_POSITION_RESPONSE.length()));

				// camera need 3s to apply new value
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					logger.error(String.format("error while control %s: %s", controllableProperty, e.getMessage()));
				}
				retrieveSimultaneous();
				populateIrisControl(stats, advancedControllableProperties);
				break;
			case GAIN:
				Gain gainControlValue = Gain.getByUIName(value);

				command = gainControlValue.getApiNameSecond();
				responseValueInHex = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				cachedLiveCameraInfo.setGain(Gain.getByAPINameSecond(responseValueInHex));
				populateGainControl(stats, advancedControllableProperties);
				break;
			case AWB:
				AWBMode awbMode = AWBMode.getByUIName(value);
				command = awbMode.getApiNameSecond();
				String response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				cachedLiveCameraInfo.setAwbMode(AWBMode.getByAPINameSecond(response));
				retrieveLiveCameraInfo(stats, advancedControllableProperties);
				populateImageAdjustControls(stats, advancedControllableProperties);
				break;
			case ND_FILTER:
				lockNightDayFilterControl(controllableProperty);
				NightDayFilter nightDayFilter = NightDayFilter.getByUIName(value);
				command = nightDayFilter.getApiName();
				response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				cachedLiveCameraInfo.setNighDayFilter(NightDayFilter.getByAPIName(response));
				populateImageAdjustControls(stats, advancedControllableProperties);
				break;
			case SHUTTER:
				Shutter shutter = Shutter.getByUIName(value);
				command = shutter.getApiNameSecond();
				response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
				cachedLiveCameraInfo.setShutter(Shutter.getByAPINameSecond(response));
				populateImageAdjustControls(stats, advancedControllableProperties);
				break;
			case IRIS_AUTO_TRIGGER:
				IrisMode irisMode = IrisMode.getByCode(value);
				command = Command.HASH.concat(irisMode.getApiName().toUpperCase());
				response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setIrisMode(IrisMode.getByAPIValue(response));
				populateImageAdjustControls(stats, advancedControllableProperties);
				break;
			case NIGHT_DAY_MODE:
				NightDayMode nightDayMode = NightDayMode.getByCode(value);
				command = Command.HASH.concat(nightDayMode.getApiName().toUpperCase());
				response = performCameraControl(command, controllableProperty, value, DeviceURL.CAMERA_PTZ_CONTROL, true);
				cachedLiveCameraInfo.setNightDayMode(NightDayMode.getByAPIValue(response));
				populateImageAdjustControls(stats, advancedControllableProperties);
				break;
			default:
				throw new IllegalStateException(String.format("zoom control %s is not supported.", controllableProperty));
		}
	}

	/**
	 * This method is used to lock night/day filter control
	 *
	 * @param controllableProperty controllable property
	 * @throws IllegalStateException when autofocus is enabled
	 */
	private void lockNightDayFilterControl(String controllableProperty) {
		if (NightDayMode.NIGHT.equals(cachedLiveCameraInfo.getNightDayMode())) {
			throw new IllegalStateException(String.format("Failed to control %s, please change the Night/Day mode to Day to control %s", controllableProperty, controllableProperty));
		}
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region power control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to handle power control
	 *
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 * @throws FailedLoginException when login fails
	 */
	private void powerControl(String controllableProperty, String value) throws FailedLoginException {
		PowerStatus powerStatus = PowerStatus.getByCode(value);
		String response = performCameraControl(Command.HASH.concat(powerStatus.getApiNameSecond()), controllableProperty, powerStatus.getUiName(), DeviceURL.CAMERA_PTZ_CONTROL, true);
		cachedLiveCameraInfo.setPowerStatus(PowerStatus.getByAPINameFirst(response));
	}

	/**
	 * This method is used to handle power control
	 *
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 * @throws FailedLoginException when login fails
	 */
	private void operationLockControl(String controllableProperty, String value) throws FailedLoginException {
		OperationLock operationLock = OperationLock.getByCode(value);
		performCameraControl(operationLock.getApiNameSecond(), controllableProperty, value, DeviceURL.CAMERA_CONTROL, true);
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region populate advanced controllable properties
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param stats store all statistics
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
			stats.put(property.getName(), DeviceConstant.EMPTY);
			advancedControllableProperties.add(property);
		}
	}

	/**
	 * This method is used to remove unused statistics/AdvancedControllableProperty from {@link CameraPanasonicAWUE150Communicator#localExtendedStatistics}
	 *
	 * @param stats Map of statistics that contains statistics to be removed
	 * @param controls Set of controls that contains AdvancedControllableProperty to be removed
	 * @param listKeys list key of statistics to be removed
	 */
	private void removeUnusedStatsAndControls(Map<String, String> stats, List<AdvancedControllableProperty> controls, Set<String> listKeys) {
		for (String key : listKeys) {
			stats.remove(key);
			controls.removeIf(advancedControllableProperty -> advancedControllableProperty.getName().equals(key));
		}
	}

	/***
	 * Create AdvancedControllableProperty slider instance
	 *
	 * @param stats extended statistics
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty slider instance
	 */
	private AdvancedControllableProperty createSlider(Map<String, String> stats, String name, String labelStart, String labelEnd, Float rangeStart, Float rangeEnd, Float initialValue) {
		stats.put(name, initialValue.toString());
		AdvancedControllableProperty.Slider slider = new AdvancedControllableProperty.Slider();
		slider.setLabelStart(labelStart);
		slider.setLabelEnd(labelEnd);
		slider.setRangeStart(rangeStart);
		slider.setRangeEnd(rangeEnd);

		return new AdvancedControllableProperty(name, new Date(), slider, initialValue);
	}

	/**
	 * Create a switch controllable property
	 *
	 * @param name name of the switch
	 * @param statusCode initial switch state (0|1)
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createSwitch(String name, String statusCode, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);
		return new AdvancedControllableProperty(name, new Date(), toggle, statusCode);
	}

	/**
	 * Instantiate Text controllable property
	 *
	 * @param name name of the property
	 * @param label default button label
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createButton(String name, String label, String labelPressed) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(0L);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}
	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	/**
	 * This method is used to convert from hex to binary string
	 *
	 * @param hexString string in hex value
	 * @return String converted string
	 */
	private String convertHexToBinary(String hexString, int binaryQuantityLimit) {
		try {
			int conHex = Integer.parseInt(hexString, 16);
			String binary = Integer.toBinaryString(conHex);
			int binaryLength = binary.length();
			for (int i = 0; i < binaryQuantityLimit - binaryLength; i++) {
				binary = '0' + binary;
			}
			return binary;
		} catch (Exception e) {
			logger.error(String.format("error while converting from hex to binary: %s", e.getMessage()), e);
		}
		return DeviceConstant.EMPTY;
	}

	/**
	 * This method is used to map preset entity status (on/off) from binary string to list preset
	 *
	 * @param preset01To40 preset entity status from preset 01 to 40 in binary string
	 * @param preset41To80 preset entity status from preset 41 to 80 in binary string
	 * @param preset81To100 preset entity status from preset 81 to 100 in binary string
	 * @return List<String> list of converted preset entity status
	 */
	private List<Character> mapPresetEntityToListPreset(String preset01To40, String preset41To80, String preset81To100) {
		if (StringUtils.isNotNullOrEmpty(preset01To40) && StringUtils.isNotNullOrEmpty(preset41To80) && StringUtils.isNotNullOrEmpty(preset81To100)) {
			String binaryString = preset81To100.concat(preset41To80).concat(preset01To40);
			List<Character> list = new ArrayList<>();

			// map binary string to list and revert index of list
			for (int i = binaryString.length() - 1; i >= 0; i--) {
				list.add(binaryString.charAt(i));
			}
			return list;
		}
		return Collections.emptyList();
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

	/**
	 * This method is used to convert from api hex string to ui  value in integer
	 *
	 * @param apiCurrentValueInHex current api value of property in hex
	 * @param apiMaxValueInHex max api value of property in hex
	 * @param apiMinValueInHex min api value of property in hex
	 * @param convertFactor UI to API convert factor
	 * @return float ui value
	 */
	private float convertFromApiValueToUIValue(String apiCurrentValueInHex, String apiMaxValueInHex, String apiMinValueInHex, float convertFactor, float uiMinValue) {
		if (StringUtils.isNotNullOrEmpty(apiCurrentValueInHex)) {
			int a = Integer.parseInt(apiCurrentValueInHex, 16) - Integer.parseInt(apiMinValueInHex, 16);
			int b = Integer.parseInt(apiMaxValueInHex, 16) - Integer.parseInt(apiMinValueInHex, 16);
			return a * convertFactor / b + uiMinValue;
		}
		return DeviceConstant.DEFAULT_VALUE;
	}

	/**
	 * This method is used to convert from ui value in integer to api hex string
	 *
	 * @param uiCurrentValue current ui value of property
	 * @param apiMaxValueInHex max api value of property in hex
	 * @param apiMinValueInHex min api value of property in hex
	 * @param convertFactor UI to API convert factor
	 * @return hString api current value in hex string
	 */
	private String convertFromUIValueToAPIValue(float uiCurrentValue, String apiMaxValueInHex, String apiMinValueInHex, float convertFactor, float uiMinValue) {
		float a = uiCurrentValue - uiMinValue;
		int b = Integer.parseInt(apiMaxValueInHex, 16) - Integer.parseInt(apiMinValueInHex, 16);
		int currentValueInInteger = (int) (b * a / convertFactor + Integer.parseInt(apiMinValueInHex, 16));
		return Integer.toHexString(currentValueInInteger);
	}

	/**
	 * Concatenates the {@code baseRequestUrl} with the uri passed in provided its not empty. Will also add the "/" if its not present in the
	 * {@code baseRequestUrl}. <br>
	 * This method has package visibility so other classes in base communicator package can access it if needed.
	 *
	 * @param uri URI to append to a base URL
	 * @return completed {@code baseRequestUrl}
	 */
	private String buildRequestUrl(String uri) {
		if (StringUtils.isNullOrEmpty(uri)) {
			return this.baseRequestUrl;
		} else if (uri.indexOf("://") > 0) {
			return uri;
		} else {
			return this.baseRequestUrl.endsWith("/") ? this.baseRequestUrl + uri : this.baseRequestUrl + "/" + uri;
		}
	}

	/**
	 * Add request headers to the prepared requestBuilder object
	 *
	 * @param requestBuilder builder object to apply headers to
	 * @return requestBuilder instance with proper authorization header specified
	 * @since 3.0.0
	 */
	private RequestBuilder processRequestHeaders(RequestBuilder requestBuilder) {
		boolean authenticationHeaderSpecified = StringUtils.isNotNullOrEmpty(authorizationHeader);
		if (authenticationHeaderSpecified) {
			requestBuilder.addHeader(HttpHeaders.AUTHORIZATION, this.authorizationHeader);
			requestBuilder.addHeader(HttpHeaders.HOST, getHost());
		} else if (StringUtils.isNotNullOrEmpty(getLogin()) || StringUtils.isNotNullOrEmpty(getPassword())) {
			requestBuilder.addHeader(WebClientConstant.AUTHORIZATION_HEADER_DEFAULT, WebClientConstant.AUTHENTICATION_METHOD_BASIC +
					DeviceConstant.SPACE + Base64.getEncoder().encodeToString(String.format("%s:%s", getLogin(), getPassword()).getBytes()));
		}
		return requestBuilder;
	}

	/**
	 * This method is used to perform camera control
	 *
	 * @param command control command
	 * @param controllableProperty controllable properties;
	 * @param controlType PTZ/CMD control type
	 * @param retryOnUnAuthorized retry on unauthorized request
	 * @return String response
	 * @throws FailedLoginException when login fails
	 * @throws IllegalStateException when exception occur
	 */
	private String performCameraControl(String command, String controllableProperty, String controlValue, String controlType, boolean retryOnUnAuthorized) throws FailedLoginException {
		try {
			String request = buildDeviceFullPath(controlType
					.concat(command)
					.concat(DeviceURL.CAMERA_CONTROL_RES));
			String response = doGet(request);
			if (StringUtils.isNullOrEmpty(response)) {
				throw new IllegalStateException("No response from device, please try again later");
			}
			if (response.toUpperCase().contains(Command.ERROR_RESPONSE_1)) {
				throw new IllegalStateException(String.format("Unsupported control value: %s", controlValue));
			}
			if (response.toUpperCase().contains(Command.ERROR_RESPONSE_2)) {
				throw new IllegalStateException("The camera is in busy status");
			}
			if (response.toUpperCase().contains(Command.ERROR_RESPONSE_3)) {
				throw new IllegalStateException(String.format("command's value is outside the acceptable range: %s", controlValue));
			}
			return response;
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				performCameraControl(command, controllableProperty, controlValue, controlType, false);
			} else {
				throw new FailedLoginException("Failed to login, please check the username and password");
			}
			return DeviceConstant.EMPTY;
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Error while controlling %s with value %s : %s", controllableProperty, controlValue, e.getMessage()), e);
		}
	}

	/**
	 * This method is used to validate input config management from user
	 *
	 * @return boolean is configManagement
	 */
	private void isValidConfigManagement() {
		isConfigManagement = StringUtils.isNotNullOrEmpty(this.configManagement) && this.configManagement.equalsIgnoreCase(DeviceConstant.IS_VALID_CONFIG_MANAGEMENT);
	}
}
