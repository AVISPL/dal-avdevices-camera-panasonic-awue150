package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150;

import java.nio.charset.StandardCharsets;
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
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.util.EntityUtils;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.AuthorizationChallengeHandler;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.Command;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceInfoMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceURL;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DevicesMetricGroup;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.WebClientConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.AutoFocus;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusADJWithPTZ;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PanTiltControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PowerOnPosition;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.preset.PresetControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto.LiveCameraInfo;
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

	private Map<String, String> failedMonitor = new HashMap<>();
	private boolean isEmergencyDelivery = false;
	private ExtendedStatistics localExtendedStatistics = new ExtendedStatistics();
	private String baseRequestUrl;

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
	private Float cachedFocusControlSpeed = DeviceConstant.MAX_FOCUS_UI_VALUE;

	/**
	 * Store current pan tilt control speed
	 */
	private Float cachedPanTiltControlSpeed = DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE;

	/**
	 * Store current preset control
	 */
	private String cachedPresetControl = DeviceConstant.DEFAULT_PRESET;

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Getting statistics from the device Panasonic camera UE150 at host %s with port %s", this.host, this.getPort()));
		}
		reentrantLock.lock();
		try {
			if (!isEmergencyDelivery) {
				final ExtendedStatistics extendedStatistics = new ExtendedStatistics();
				final Map<String, String> stats = new HashMap<>();
				final List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
				login();
				retrieveSystemInfo(stats, true);
				retrieveLiveCameraInfo(stats, true);
				retrieveModelName(stats, true);
				if (OperationLock.UNLOCK.equals(cachedLiveCameraInfo.getOperationLock()) && PowerStatus.ON.equals(cachedLiveCameraInfo.getPowerStatus())) {
					populateFocusControls(stats, advancedControllableProperties);
					populatePresetControls(stats, advancedControllableProperties);
					populatePanTiltControls(stats, advancedControllableProperties);
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
					default:
						throw new IllegalStateException(String.format("Control group %s is not supported", splitProperty[0]));
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
	 * @return HttpHeaders contain cookie for authorization
	 */
	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		if (StringUtils.isNotNullOrEmpty(authorizationHeader)) {
			headers.set(AuthorizationChallengeHandler.AUTHORIZATION, authorizationHeader);
			headers.set("Host", getHost());
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
	 * This method is used to retrieve system information by send get request to "http://10.8.53.221/cgi-bin/getinfo?FILE=1"
	 *
	 * @param stats When there is no response data, the failedMonitor is going to update
	 * @param retryOnUnAuthorized retry on unauthorized request
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveSystemInfo(Map<String, String> stats, boolean retryOnUnAuthorized) {
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
				} else {
					updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
				}
			} else {
				updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting system information");
			}
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				retrieveSystemInfo(stats, false);
			}
		} catch (Exception e) {
			updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), String.format("error while getting system information: %s", e.getMessage()));
		}
	}

	/**
	 * This method is used to retrieve live camera information by send get request to "http://10.8.53.221/live/camdata.html"
	 *
	 * @param stats When there is no response data, the failedMonitor is going to update
	 * @param retryOnUnAuthorized retry on unauthorized request
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveLiveCameraInfo(Map<String, String> stats, boolean retryOnUnAuthorized) {
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
					if (fieldElement.startsWith("axf")) {
						fields.put("afx", fieldElement.substring("afx".length()));
						continue;
					}
					if (fieldElement.startsWith("TITLE:")) {
						fields.put("TITLE:", fieldElement.substring("TITLE:".length()));
						continue;
					}
					if (fieldElement.startsWith("s")) {
						cachedLiveCameraInfo.setLastPreset(fieldElement.substring("s".length()));
						continue;
					}
					if (fieldElement.startsWith("aPC")) {
						cachedLiveCameraInfo.setPanTiltPosition(fieldElement.substring("aPC".length()));
						continue;
					}
					if (fieldElement.startsWith("pE00")) {
						cachedLiveCameraInfo.setPreset01To40(convertHexToBinary(fieldElement.substring("pE00".length()), 40));
						continue;
					}
					if (fieldElement.startsWith("pE01")) {
						cachedLiveCameraInfo.setPreset41To80(convertHexToBinary(fieldElement.substring("pE01".length()), 40));
						continue;
					}
					if (fieldElement.startsWith("pE02")) {
						cachedLiveCameraInfo.setPreset81To100(convertHexToBinary(fieldElement.substring("pE02".length()), 20));
						continue;
					}
					fields.put(fieldElement, DeviceConstant.NONE);
				}
				// put live camera info to cache
				cachedLiveCameraInfo.setFocusPosition(fields.get("afx"));
				cachedLiveCameraInfo.setAutoFocus(AutoFocus.getByAPIValue(fields));
				cachedLiveCameraInfo.setFocusADJWithPTZ(FocusADJWithPTZ.getByAPIValue(fields).getUiName());
				cachedLiveCameraInfo.setOperationLock(OperationLock.getByAPIValue(fields));
				cachedLiveCameraInfo.setPowerStatus(PowerStatus.getByAPIValue(fields));
				cachedLiveCameraInfo.setPowerOnPosition(PowerOnPosition.getByAPIValue(fields));
				cachedLiveCameraInfo.setPresetEntitiesStatus(
						mapPresetEntityToListPreset(cachedLiveCameraInfo.getPreset01To40(), cachedLiveCameraInfo.getPreset41To80(), cachedLiveCameraInfo.getPreset81To100()));

				// populate live camera info
				stats.put(DeviceInfoMetric.OUTPUT_FORMAT.getName(), OutputFormatMetric.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.OPERATION_LOCK.getName(), cachedLiveCameraInfo.getOperationLock().getUiName());
				stats.put(DeviceInfoMetric.CAMERA_TITLE.getName(), getDefaultValueForNullData(fields.get("TITLE:"), DeviceConstant.NONE));
				stats.put(DeviceInfoMetric.FAN1_STATUS.getName(), Fan1Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.FAN2_STATUS.getName(), Fan2Status.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_INFORMATION.getName(), ErrorInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.ERROR_STATUS_INFO.getName(), ErrorStatusInformation.getByAPIValue(fields).getUiName());
				stats.put(DeviceInfoMetric.POWER_STATUS.getName(), cachedLiveCameraInfo.getPowerStatus().getUiName());
			} else {
				updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), "error while getting camera live information");
			}
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				retrieveLiveCameraInfo(stats, false);
			}
		} catch (Exception e) {
			updateFailedMonitor(DevicesMetricGroup.CAMERA_LIVE_INFO.getName(), String.format("error while getting camera live information: %s", e.getMessage()));
		}
	}

	/**
	 * This method is used to retrieve model name information by send get request to "http://10.8.53.221/cgi-bin/model_serial"
	 *
	 * @param stats When there is no response data, the failedMonitor is going to update
	 * @param retryOnUnAuthorized retry on unauthorized request
	 * When there is an exception, the failedMonitor is going to update and exception is not populated
	 */
	private void retrieveModelName(Map<String, String> stats, boolean retryOnUnAuthorized) {
		try {
			String request = buildDeviceFullPath(DeviceURL.MODEL_SERIAL);
			String response = doGet(request);
			String[] splitResponse = response.split(DeviceConstant.COLON);
			if (StringUtils.isNotNullOrEmpty(response)) {
				stats.put(DeviceInfoMetric.MODEL_NAME.getName(), splitResponse[0]);
			} else {
				updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), "error while getting model name");
			}
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				retrieveSystemInfo(stats, false);
			}
		} catch (Exception e) {
			updateFailedMonitor(DevicesMetricGroup.SYSTEM_INFO.getName(), String.format("error while model name: %s", e.getMessage()));
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

		String focusControlLabelStart = String.valueOf(DeviceConstant.MIN_FOCUS_UI_VALUE.intValue());
		String focusControlLabelEnd = String.valueOf(DeviceConstant.MAX_FOCUS_UI_VALUE.intValue());
		String focusSpeedControlLabelStart = String.valueOf(DeviceConstant.MIN_FOCUS_UI_VALUE.intValue() + 1);
		Float currentFocusPosition = convertFromApiValueToUIValue(cachedLiveCameraInfo.getFocusPosition(),
				DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE, DeviceConstant.MIN_FOCUS_UI_VALUE);
		addAdvanceControlProperties(advancedControllableProperties, createSlider(stats, groupName.concat(FocusControlMetric.FOCUS_CONTROL.getName()), focusControlLabelStart, focusControlLabelEnd,
				DeviceConstant.MIN_FOCUS_UI_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE, currentFocusPosition));
		addAdvanceControlProperties(advancedControllableProperties,
				createSlider(stats, groupName.concat(FocusControlMetric.FOCUS_CONTROL_SPEED.getName()), focusSpeedControlLabelStart, focusControlLabelEnd,
						DeviceConstant.MIN_FOCUS_SPEED_UI_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE, cachedFocusControlSpeed));
		addAdvanceControlProperties(advancedControllableProperties,
				createButton(stats, groupName.concat(FocusControlMetric.FOCUS_CONTROL_NEAR.getName()), FocusControlMetric.NEAR.getName(), DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties,
				createButton(stats, groupName.concat(FocusControlMetric.FOCUS_CONTROL_FAR.getName()), FocusControlMetric.FAR.getName(), DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties,
				createSwitch(stats, groupName.concat(FocusControlMetric.AUTO_FOCUS.getName()), cachedLiveCameraInfo.getAutoFocus().getCode(), DeviceConstant.OFF, DeviceConstant.ON));
		stats.put(groupName.concat(FocusControlMetric.FOCUS_ADJ_WITH_PTZ.getName()), cachedLiveCameraInfo.getFocusADJWithPTZ());
		stats.put(groupName.concat(FocusControlMetric.FOCUS_MODE.getName()), cachedLiveCameraInfo.getAutoFocus().getUiName());
		stats.put(groupName.concat(FocusControlMetric.FOCUS_CONTROL.getName()).concat(FocusControlMetric.CURRENT_VALUE.getName()), currentFocusPosition.toString());
		stats.put(groupName.concat(FocusControlMetric.FOCUS_CONTROL_SPEED.getName()).concat(FocusControlMetric.CURRENT_VALUE.getName()), cachedFocusControlSpeed.toString());
	}

	/**
	 * This method is used to populate focus control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 */
	private void focusControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws FailedLoginException {
		FocusControlMetric focusControlMetric = FocusControlMetric.getByName(controllableProperty);
		float convertFromUIValueToAPIValueFactor =
				(Integer.parseInt(DeviceConstant.MAX_FOCUS_API_VALUE, 16) - Integer.parseInt(DeviceConstant.MIN_FOCUS_API_VALUE, 16)) / DeviceConstant.MAX_FOCUS_UI_VALUE;
		isEmergencyDelivery = true;
		switch (focusControlMetric) {
			case FOCUS_CONTROL:
				lockFocusControl(controllableProperty);
				Float currentValue = (float) Math.ceil(Float.parseFloat(value)) * convertFromUIValueToAPIValueFactor + Integer.parseInt(DeviceConstant.MIN_FOCUS_API_VALUE, 16);

				String currentValueInHex = Integer.toHexString(currentValue.intValue()).toUpperCase();
				String responseValueInHex = performFocusControl(currentValueInHex, controllableProperty, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_FAR:
				lockFocusControl(controllableProperty);
				Float currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getFocusPosition(),
						DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE, DeviceConstant.MAX_FOCUS_UI_VALUE, DeviceConstant.MIN_FOCUS_UI_VALUE) + cachedFocusControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_FOCUS_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_FOCUS_UI_VALUE;
				}
				Float currentAPIValue = (float) Math.ceil(currentUIValue) * convertFromUIValueToAPIValueFactor + Integer.parseInt(DeviceConstant.MIN_FOCUS_API_VALUE, 16);
				currentValueInHex = Integer.toHexString(currentAPIValue.intValue()).toUpperCase();
				responseValueInHex = performFocusControl(currentValueInHex, controllableProperty, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_NEAR:
				lockFocusControl(controllableProperty);
				currentUIValue = convertFromApiValueToUIValue(cachedLiveCameraInfo.getFocusPosition(), DeviceConstant.MAX_FOCUS_API_VALUE, DeviceConstant.MIN_FOCUS_API_VALUE,
						DeviceConstant.MAX_FOCUS_UI_VALUE, DeviceConstant.MIN_FOCUS_UI_VALUE) - cachedFocusControlSpeed;
				if (currentUIValue > DeviceConstant.MAX_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MAX_FOCUS_UI_VALUE;
				}
				if (currentUIValue < DeviceConstant.MIN_FOCUS_UI_VALUE) {
					currentUIValue = DeviceConstant.MIN_FOCUS_UI_VALUE;
				}
				currentAPIValue = (float) Math.ceil(currentUIValue) * convertFromUIValueToAPIValueFactor + Integer.parseInt(DeviceConstant.MIN_FOCUS_API_VALUE, 16);
				currentValueInHex = Integer.toHexString(currentAPIValue.intValue()).toUpperCase();
				responseValueInHex = performFocusControl(currentValueInHex, controllableProperty, true);
				cachedLiveCameraInfo.setFocusPosition(responseValueInHex.substring(Command.FOCUS.length()));
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case FOCUS_CONTROL_SPEED:
				cachedFocusControlSpeed = (float) Math.ceil(Float.parseFloat(value));
				populateFocusControls(stats, advancedControllableProperties);
				break;
			case AUTO_FOCUS:
				AutoFocus autoFocus = AutoFocus.getByCode(value);
				String response = performAutoFocusControl(autoFocus.getCode(), controllableProperty, true);
				autoFocus = AutoFocus.getByApiName2(response);
				cachedLiveCameraInfo.setAutoFocus(autoFocus);
				populateFocusControls(stats, advancedControllableProperties);
				break;
			default:
				throw new IllegalStateException(String.format("focus control %s is not supported.", controllableProperty));
		}
	}

	/**
	 * This method is used to lock focus control
	 *
	 * @throws IllegalStateException when autofocus is enabled
	 */
	private void lockFocusControl(String controllableProperty) {
		if (cachedLiveCameraInfo.getAutoFocus().equals(AutoFocus.AUTO)) {
			throw new IllegalStateException(String.format("Failed to control %s, please changing focus mode to manual to control %s", controllableProperty, controllableProperty));
		}
	}

	/**
	 * This method is used to perform focus control
	 *
	 * @param command focus control command
	 * @param controllableProperty controllable properties;
	 * @param retryOnUnAuthorized retry on unauthorized request
	 */
	private String performFocusControl(String command, String controllableProperty, boolean retryOnUnAuthorized) throws FailedLoginException {
		try {
			String request = buildDeviceFullPath(DeviceURL.CAMERA_PTZ_CONTROL
					.concat(DeviceURL.CAMERA_CONTROL_HASH)
					.concat(Command.FOCUS)
					.concat(command)
					.concat(DeviceURL.CAMERA_CONTROL_RES));
			String response = doGet(request);
			return response;
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				performFocusControl(command, controllableProperty, false);
			}
			throw new FailedLoginException("Failed to login, please check the username and password");
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Error while controlling %s: %s", controllableProperty, e.getMessage()), e);
		}
	}

	/**
	 * This method is used to perform focus control
	 *
	 * @param command focus control command
	 * @param controllableProperty controllable properties;
	 * @return String response
	 * @throws FailedLoginException when log in failed
	 * @throws IllegalStateException when exception occur
	 */
	private String performAutoFocusControl(String command, String controllableProperty, boolean retryOnUnAuthorized) throws FailedLoginException {
		try {
			String request = buildDeviceFullPath(DeviceURL.CAMERA_CONTROL
					.concat(Command.AUTO_FOCUS)
					.concat(command)
					.concat(DeviceURL.CAMERA_CONTROL_RES));
			String response = doGet(request);
			return response;
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				performAutoFocusControl(command, controllableProperty, false);
			}
			throw new FailedLoginException("Failed to login, please check the username and password");
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Error while controlling %s: %s", controllableProperty, e.getMessage()), e);
		}
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region preset control
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * This method is used to populate focus control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 */
	private void populatePresetControls(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
		String groupName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH;
		if (presetModes.isEmpty()) {
			presetModes.add(DeviceConstant.DEFAULT_PRESET);
			for (int index = 1; index <= 100; index++) {
				presetModes.add(PresetControlMetric.PRESET.getName().concat(String.format("%03d", index)));
			}
		}
		addAdvanceControlProperties(advancedControllableProperties, createDropdown(stats, groupName.concat(PresetControlMetric.PRESET.getName()), presetModes, cachedPresetControl));

		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PresetControlMetric.HOME.getName()), DeviceConstant.HOME, DeviceConstant.PUSHING));
		if (!DeviceConstant.DEFAULT_PRESET.equals(cachedPresetControl)) {
			addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PresetControlMetric.DELETE_PRESET.getName()), DeviceConstant.DELETE, DeviceConstant.DELETING));
			addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PresetControlMetric.APPLY_PRESET.getName()), DeviceConstant.APPLY, DeviceConstant.APPLYING));
			addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PresetControlMetric.SET_PRESET.getName()), DeviceConstant.SET, DeviceConstant.SETTING));
		} else {
			Set<String> unusedKeys = new HashSet<>();
			unusedKeys.add(groupName.concat(PresetControlMetric.DELETE_PRESET.getName()));
			unusedKeys.add(groupName.concat(PresetControlMetric.APPLY_PRESET.getName()));
			unusedKeys.add(groupName.concat(PresetControlMetric.SET_PRESET.getName()));
			removeUnusedStatsAndControls(stats, advancedControllableProperties, unusedKeys);
		}
		stats.put(groupName.concat(PresetControlMetric.LAST_PRESET.getName()), PresetControlMetric.PRESET.getName() + String.format("%03d", Integer.parseInt(cachedLiveCameraInfo.getLastPreset())));
	}

	/**
	 * This method is used to populate focus control
	 *
	 * @param stats store all statistics
	 * @param advancedControllableProperties store all controllable properties
	 * @param controllableProperty controllable property
	 * @param value value of controllable property
	 */
	private void presetControl(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties, String controllableProperty, String value) throws FailedLoginException {
		PresetControlMetric presetControlMetric = PresetControlMetric.getByName(controllableProperty);
		String presetIndexAPIValue = "-1";
		if (!DeviceConstant.DEFAULT_PRESET.equals(cachedPresetControl)) {
			Integer index = Integer.parseInt(cachedPresetControl.substring(DeviceConstant.PRESET.length())) - 1;
			presetIndexAPIValue = String.format("%02d", index);
		}
		isEmergencyDelivery = true;
		switch (presetControlMetric) {
			case PRESET:
				cachedPresetControl = value;
				break;
			case SET_PRESET:
				String command = Command.SET_PRESET.concat(presetIndexAPIValue);
				performPresetControl(command, controllableProperty, true);
				List<Character> presetEntitiesStatus = cachedLiveCameraInfo.getPresetEntitiesStatus();
				presetEntitiesStatus.set(Integer.parseInt(presetIndexAPIValue), '1');
				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				break;
			case DELETE_PRESET:
				command = Command.DELETE_PRESET.concat(presetIndexAPIValue);
				performPresetControl(command, controllableProperty, true);
				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				break;
			case APPLY_PRESET:
				if (cachedLiveCameraInfo.getPresetEntitiesStatus().get(Integer.parseInt(presetIndexAPIValue)).equals('0')) {
					throw new IllegalStateException(String.format("%s is not set, please try another preset", value));
				}
				command = Command.APPLY_PRESET.concat(presetIndexAPIValue);
				String response = performPresetControl(command, controllableProperty, true);
				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				cachedLiveCameraInfo.setLastPreset(String.valueOf(Integer.parseInt(response.substring("s".length())) + 1));
				break;
			case HOME:
				performPresetControl(Command.HOME_PRESET, controllableProperty, true);
				cachedPresetControl = DeviceConstant.DEFAULT_PRESET;
				break;
			default:
				throw new IllegalStateException(String.format("preset control %s is not supported.", controllableProperty));
		}
		populatePresetControls(stats, advancedControllableProperties);
	}

	/**
	 * This method is used to perform focus control
	 *
	 * @param command focus control command
	 * @param controllableProperty controllable properties;
	 * @return String response
	 * @throws FailedLoginException when log in failed
	 * @throws IllegalStateException when exception occur
	 */
	private String performPresetControl(String command, String controllableProperty, boolean retryOnUnAuthorized) throws FailedLoginException {
		try {
			String request = buildDeviceFullPath(DeviceURL.CAMERA_PTZ_CONTROL
					.concat(DeviceURL.CAMERA_CONTROL_HASH)
					.concat(command)
					.concat(DeviceURL.CAMERA_CONTROL_RES));
			String response = doGet(request);
			if (response.toUpperCase().contains("ER")) {
				throw new IllegalStateException(String.format("Error while controlling %s", controllableProperty));
			}
			return response;
		} catch (FailedLoginException f) {
			if (retryOnUnAuthorized) {
				login();
				performPresetControl(command, controllableProperty, false);
			}
			throw new FailedLoginException("Failed to login, please check the username and password");
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Error while controlling %s: %s", controllableProperty, e.getMessage()), e);
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
		String groupName = DevicesMetricGroup.PAN_TITL_PAD_CONTROL.getName() + DeviceConstant.HASH;

		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.HOME.getName()), DeviceConstant.PT_HOME, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.UP.getName()), DeviceConstant.UP, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.DOWN.getName()), DeviceConstant.DOWN, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.LEFT.getName()), DeviceConstant.LEFT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.RIGHT.getName()), DeviceConstant.RIGHT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.UP_LEFT.getName()), DeviceConstant.UP_LEFT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.UP_RIGHT.getName()), DeviceConstant.UP_RIGHT, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.DOWN_LEFT.getName()), DeviceConstant.DOWN_LEFT_, DeviceConstant.PUSHING));
		addAdvanceControlProperties(advancedControllableProperties, createButton(stats, groupName.concat(PanTiltControlMetric.DOWN_RIGHT.getName()), DeviceConstant.DOWN_RIGHT, DeviceConstant.PUSHING));

		String panTiltSpeedLabelName = groupName.concat(PanTiltControlMetric.PT_SPEED.getName());
		String labelStart = String.valueOf((int) DeviceConstant.MIN_PAN_TILT_SPEED_UI_VALUE);
		String labelEnd = String.valueOf((int) DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE);

		addAdvanceControlProperties(advancedControllableProperties,
				createSlider(stats, panTiltSpeedLabelName, labelStart, labelEnd, DeviceConstant.MIN_PAN_TILT_SPEED_UI_VALUE, DeviceConstant.MAX_PAN_TILT_SPEED_UI_VALUE, cachedPanTiltControlSpeed));
		stats.put(groupName.concat(PanTiltControlMetric.POWER_ON_POSITION.getName()), cachedLiveCameraInfo.getPowerOnPosition().getUiName());
		stats.put(groupName.concat(PanTiltControlMetric.PT_SPEED_CURRENT_VALUE.getName()), String.valueOf(cachedPanTiltControlSpeed.intValue()));
		stats.put(groupName.concat(PanTiltControlMetric.PT_ABSOLUTE_POSITION_CONTROL.getName()), convertPanTiltPositionFromApiToUiValue(cachedLiveCameraInfo.getPanTiltPosition()));
	}

	/**
	 * This method is used to populate pan tilt control
	 */
	private String convertPanTiltPositionFromApiToUiValue(String panTiltData) {

		if (panTiltData.length() == (DeviceConstant.DEFAULT_PAN_POSITION.length() + DeviceConstant.DEFAULT_TILT_POSITION.length())) {
			String currentPanValue = panTiltData.substring(0, DeviceConstant.DEFAULT_PAN_POSITION.length());
			String currentTiltValue = panTiltData.substring(DeviceConstant.DEFAULT_PAN_POSITION.length());
			String panPosition = String.valueOf((int) convertFromApiValueToUIValue(currentPanValue, DeviceConstant.MAX_PAN_POSITION_API_VALUE,
					DeviceConstant.MIN_PAN_POSITION_API_VALUE, DeviceConstant.MAX_PAN_POSITION_UI, DeviceConstant.MIN_PAN_POSITION_UI));
			String tiltPosition = String.valueOf((int) convertFromApiValueToUIValue(currentTiltValue, DeviceConstant.MAX_TILT_POSITION_API_VALUE,
					DeviceConstant.MIN_TILT_POSITION_API_VALUE, DeviceConstant.MAX_TILT_POSITION_UI, DeviceConstant.MIN_TILT_POSITION_UI));
			return String.format("Pan %s, tilt %s", panPosition, tiltPosition);
		}
		return DeviceConstant.NONE;
	}

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region zoom controllable properties
	//--------------------------------------------------------------------------------------------------------------------------------


	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

	//region populate advanced controllable properties
	//--------------------------------------------------------------------------------------------------------------------------------

	/**
	 * Add advancedControllableProperties if advancedControllableProperties different empty
	 *
	 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
	 * @param property the property is item advancedControllableProperties
	 * @return String response
	 * @throws FailedLoginException when log in failed
	 * @throws IllegalStateException when exception occur
	 */
	private void addAdvanceControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, AdvancedControllableProperty property) {
		if (property != null) {
			for (AdvancedControllableProperty controllableProperty : advancedControllableProperties) {
				if (controllableProperty.getName().equals(property.getName())) {
					advancedControllableProperties.remove(controllableProperty);
					break;
				}
			}
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

	/**
	 * Instantiate Text controllable property
	 *
	 * @param stats extended statistics
	 * @param name name of the property
	 * @param label default button label
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createButton(Map<String, String> stats, String name, String label, String labelPressed) {
		AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
		stats.put(name, label);
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(0L);
		return new AdvancedControllableProperty(name, new Date(), button, "");
	}

	/**
	 * Create a switch controllable property
	 *
	 * @param stats extended statistics
	 * @param name name of the switch
	 * @param statusCode initial switch state (0|1)
	 * @return AdvancedControllableProperty button instance
	 */
	private AdvancedControllableProperty createSwitch(Map<String, String> stats, String name, String statusCode, String labelOff, String labelOn) {
		AdvancedControllableProperty.Switch toggle = new AdvancedControllableProperty.Switch();
		toggle.setLabelOff(labelOff);
		toggle.setLabelOn(labelOn);
		stats.put(name, String.valueOf(statusCode));
		return new AdvancedControllableProperty(name, new Date(), toggle, statusCode);
	}

	/***
	 * Create AdvancedControllableProperty preset instance
	 *
	 * @param stats extended statistics
	 * @param name name of the control
	 * @param initialValue initial value of the control
	 * @return AdvancedControllableProperty preset instance
	 */
	private AdvancedControllableProperty createDropdown(Map<String, String> stats, String name, List<String> values, String initialValue) {
		stats.put(name, initialValue);
		AdvancedControllableProperty.DropDown dropDown = new AdvancedControllableProperty.DropDown();
		dropDown.setOptions(values.toArray(new String[0]));
		dropDown.setLabels(values.toArray(new String[0]));

		return new AdvancedControllableProperty(name, new Date(), dropDown, initialValue);
	}

	/**
	 * Create a controllable property Numeric
	 *
	 * @param stats extended statistics
	 * @param name the name of property
	 * @param initialValue character String
	 * @return AdvancedControllableProperty Text instance
	 */
	private AdvancedControllableProperty createNumeric(Map<String, String> stats, String name, String initialValue) {
		stats.put(name, initialValue);
		AdvancedControllableProperty.Numeric numeric = new AdvancedControllableProperty.Numeric();
		return new AdvancedControllableProperty(name, new Date(), numeric, initialValue);
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

	//--------------------------------------------------------------------------------------------------------------------------------
	//endregion

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
	 * This method is used to convert from hex to binary string
	 *
	 * @param hexString string in hex value
	 * @return String converted string
	 */
	private String convertHexToBinary(String hexString, int binaryQuantityLimit) {
		try {
			int conHex = Integer.parseInt(hexString, 16);
			String binary = Integer.toBinaryString(conHex);
			String regex = "%0" + binaryQuantityLimit + "d";
			return String.format(regex, Integer.parseInt(binary));
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
		String binaryString = preset81To100.concat(preset41To80).concat(preset01To40);
		List<Character> b = binaryString.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
		List<Character> a = new ArrayList<>();
		for (int i = b.size() - 1; i > 0; i--){
			a.add(b.get(i));
		}
		return a;
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
	 * This method is used to convert from api hex value to ui integer value
	 *
	 * @param apiCurrentValueInHex current api value of properties in hex
	 * @param apiMaxValueInHex max api value of property in hex
	 * @param apiMinValueInHex min api value of property in hex
	 * @param uiMaxValue max ui value of properties
	 * @return float ui value
	 */
	private float convertFromApiValueToUIValue(String apiCurrentValueInHex, String apiMaxValueInHex, String apiMinValueInHex, float uiMaxValue, float uiMinValue) {
		int a = Integer.parseInt(apiCurrentValueInHex, 16) - Integer.parseInt(apiMinValueInHex, 16);
		int b = Integer.parseInt(apiMaxValueInHex, 16) - Integer.parseInt(apiMinValueInHex, 16);
		return (float) Math.ceil(a * (uiMaxValue - uiMinValue) / b);
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

			HttpEntity httpEntity = response.getEntity();
			if (httpEntity != null) {
				// response body
				return EntityUtils.toString(httpEntity);
			}
		} finally {
			if (response instanceof CloseableHttpResponse) {
				((CloseableHttpResponse) response).close();
			}
		}
		return null;
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
}
