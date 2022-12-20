/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.DropDown;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.CameraPanasonicAWUE150Communicator;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceInfoMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DevicesMetricGroup;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.AWBMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.ImageAdjustControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.NightDayFilter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.Shutter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PanTiltControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.preset.PresetControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.zoom.ZoomControlMetric;

/**
 * Unit test for CameraPanasonicAWUE150Communicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 26/10/2022
 * @version 1.0
 * @since 1.0
 */
@Tag("RealDevice")
class CameraPanasonicAWUE150CommunicatorTest {
	private final CameraPanasonicAWUE150Communicator cameraPanasonicAWUE150Communicator = new CameraPanasonicAWUE150Communicator();

	@BeforeEach()
	public void setUp() throws Exception {
		cameraPanasonicAWUE150Communicator.setHost("10.8.53.221");
		cameraPanasonicAWUE150Communicator.setPort(443);
		cameraPanasonicAWUE150Communicator.setLogin("admin");
		cameraPanasonicAWUE150Communicator.setPassword("admin");
		cameraPanasonicAWUE150Communicator.setTrustAllCertificates(true);
		cameraPanasonicAWUE150Communicator.setConfigManagement("true");
		cameraPanasonicAWUE150Communicator.init();
		cameraPanasonicAWUE150Communicator.connect();
	}

	@AfterEach()
	public void destroy() throws Exception {
		cameraPanasonicAWUE150Communicator.disconnect();
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.getMultipleStatistics successful with valid username password
	 * Expected retrieve valid device monitoring data
	 */
	@Test
	void testHaivisionX4DecoderCommunicatorGetMonitoringDataSuccessful() throws Exception {
		ExtendedStatistics statistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.MAC_ADDRESS.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.SERIAL_NUMBER.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.FIRMWARE_VERSION.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.MODEL_NAME.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.FAN1_STATUS.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.FAN2_STATUS.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.ERROR_INFORMATION.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.ERROR_STATUS_INFO.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.OPERATION_LOCK.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.POWER_STATUS.getName()));
		Assert.assertNotNull(statistics.getStatistics().get(DeviceInfoMetric.OUTPUT_FORMAT.getName()));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.getMultipleStatistics failed with invalid username password
	 * Expected throw login exception
	 */
	@Test
	void testCameraAWUE150CommunicatorGetMonitoringDataFailed() throws Exception {
		CameraPanasonicAWUE150Communicator cameraPanasonicAWUE150CommunicatorSpy = Mockito.spy(CameraPanasonicAWUE150Communicator.class);
		cameraPanasonicAWUE150CommunicatorSpy.setHost("10.8.53.221");
		cameraPanasonicAWUE150CommunicatorSpy.setLogin("adminv");
		cameraPanasonicAWUE150CommunicatorSpy.setPassword("adminv");
		ResourceNotReachableException exception =
				Assertions.assertThrows(ResourceNotReachableException.class, () -> {
					cameraPanasonicAWUE150CommunicatorSpy.getMultipleStatistics().get(0);
				});
		Assertions.assertEquals("Login failed, Please check the username and password", exception.getMessage());
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: Change focus control value
	 *
	 * Expected: control successfully
	 */
	@Test
	void testFocusControl() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName();
		String propertyValue = "50";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(propertyValue, stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus far
	 *
	 * Expected: control successfully
	 */
	@Test
	void testFocusControlFar() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL_FAR.getName();
		String propertyCurrentValueName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName() + FocusControlMetric.CURRENT_VALUE.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(DeviceConstant.MAX_FOCUS_UI_VALUE, Float.parseFloat(stats.get(propertyCurrentValueName)));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: Change focus control value
	 *
	 * Expected: control successfully
	 */
	@Test
	void testFocusControlSpeed() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL_SPEED.getName();
		String propertyValue = "50.0";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(propertyValue, stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus near
	 *
	 * Expected: control successfully
	 */
	@Test
	void testFocusControlNear() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL_NEAR.getName();
		String propertyCurrentValueName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName() + FocusControlMetric.CURRENT_VALUE.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(DeviceConstant.MIN_FOCUS_UI_VALUE, Float.parseFloat(stats.get(propertyCurrentValueName)));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus near
	 *
	 * Expected: control successfully
	 */
	@Test
	void testFocusControlNearWithChangedSpeed() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String focusControlName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName();
		String focusSpeedControlName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL_SPEED.getName();
		String focusCurrentValueName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName() + FocusControlMetric.CURRENT_VALUE.getName();
		String focusNearName = DevicesMetricGroup.FOCUS_CONTROL.getName() + DeviceConstant.HASH + FocusControlMetric.FOCUS_CONTROL.getName() + FocusControlMetric.NEAR.getName();
		String propertyValue = String.valueOf(DeviceConstant.MAX_FOCUS_UI_VALUE);

		// change focus value to max
		controllableProperty.setProperty(focusControlName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();

		for (int i = (int) DeviceConstant.MIN_FOCUS_SPEED_UI_VALUE; i <= DeviceConstant.MAX_FOCUS_SPEED_UI_VALUE; i++) {
			// control speed
			propertyValue = String.valueOf(i);
			controllableProperty.setProperty(focusSpeedControlName);
			controllableProperty.setValue(propertyValue);

			cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();

			// control focus near
			propertyValue = "1";
			controllableProperty.setProperty(focusNearName);
			controllableProperty.setValue(propertyValue);

			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			System.out.println(stats.get(focusControlName) + " " + stats.get(focusCurrentValueName) + "\n");
			Assertions.assertEquals(Float.parseFloat(stats.get(focusControlName)), Float.parseFloat(stats.get(focusCurrentValueName)));
		}
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty preset control: set preset
	 *
	 * Expected: control successfully
	 */
	@Test
	void testSetPreset() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = "Preset100";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.SET_PRESET.getName();
		propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty preset control: delete preset
	 *
	 * Expected: control successfully
	 */
	@Test
	void testDeletePreset() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = "100 Preset100";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.DELETE_PRESET.getName();
		propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty preset control: apply preset
	 *
	 * Expected: control successfully
	 */
	@Test
	void testApplyPreset() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = DeviceConstant.DEFAULT_PRESET;
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();

		propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.APPLY_PRESET.getName();
		propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();

		Assertions.assertNull(stats.get(propertyName));
		String lastPreset = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.LAST_PRESET.getName();
		Assertions.assertEquals("TMA", stats.get(lastPreset));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push up
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltUp() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.UP.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push down
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltDown() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.DOWN.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push left
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltLeft() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.LEFT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push right
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltRight() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.RIGHT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push up left
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltUpLeft() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.UP_LEFT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push up right
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltUpRight() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.UP_RIGHT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push down left
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltDownLeft() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.DOWN_LEFT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push down right
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltDownRight() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.DOWN_RIGHT.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty pan tilt control: push home
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltHome() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PAN_TILT_PAD_CONTROL.getName() + DeviceConstant.HASH + PanTiltControlMetric.HOME.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty zoom control: Change zoom control value
	 *
	 * Expected: control successfully
	 */
	@Test
	void testZoomControl() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName();
		String propertyValue = "100";
		String propertyCurrentValueName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName() + ZoomControlMetric.CURRENT_VALUE.getName();
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();

		Assertions.assertEquals(propertyValue, stats.get(propertyCurrentValueName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus far
	 *
	 * Expected: control successfully
	 */
	@Test
	void testZoomControlFar() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_IN.getName();
		String propertyCurrentValueName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName() + ZoomControlMetric.CURRENT_VALUE.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(DeviceConstant.MAX_ZOOM_UI_VALUE, Float.parseFloat(stats.get(propertyCurrentValueName)));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: Change focus control value
	 *
	 * Expected: control successfully
	 */
	@Test
	void testZoomControlSpeed() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL_SPEED.getName();
		String propertyValue = "0";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(propertyValue, stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus near
	 *
	 * Expected: control successfully
	 */
	@Test
	void testZoomControlNear() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_OUT.getName();
		String propertyCurrentValueName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName() + ZoomControlMetric.CURRENT_VALUE.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(DeviceConstant.MIN_ZOOM_UI_VALUE, Float.parseFloat(stats.get(propertyCurrentValueName)));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus near
	 *
	 * Expected: control successfully
	 */
	@Test
	void testZoomControlNearWithChangedSpeed() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String zoomControlName = DevicesMetricGroup.ZOOM_CONTROL.getName() + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName();
		String zoomSpeedControlName = DevicesMetricGroup.ZOOM_CONTROL.getName()  + DeviceConstant.HASH + ZoomControlMetric.ZOOM_CONTROL.getName();
		String zoomCurrentValueName = DevicesMetricGroup.ZOOM_CONTROL.getName()  + DeviceConstant.HASH +ZoomControlMetric.ZOOM_CONTROL.getName() + ZoomControlMetric.CURRENT_VALUE.getName();
		String zoomTeleName = DevicesMetricGroup.ZOOM_CONTROL.getName()  + DeviceConstant.HASH + ZoomControlMetric.ZOOM_IN;
		String propertyValue = String.valueOf(DeviceConstant.MAX_ZOOM_UI_VALUE);

		// change focus value to max
		controllableProperty.setProperty(zoomControlName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();

		for (int i = (int) DeviceConstant.MIN_ZOOM_SPEED_UI_VALUE; i <= DeviceConstant.MAX_ZOOM_SPEED_UI_VALUE; i++) {
			// control speed
			propertyValue = String.valueOf(i);
			controllableProperty.setProperty(zoomSpeedControlName);
			controllableProperty.setValue(propertyValue);

			cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();

			// control focus near
			propertyValue = "1";
			controllableProperty.setProperty(zoomTeleName);
			controllableProperty.setValue(propertyValue);

			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(Float.parseFloat(stats.get(zoomControlName)), Float.parseFloat(stats.get(zoomCurrentValueName)));
		}
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty shutter control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testShutterControl() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.SHUTTER.getName();
		String propertyValue;
		controllableProperty.setProperty(propertyName);

		for (Shutter shutter : Shutter.values()) {
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			propertyValue = shutter.getUiName();
			controllableProperty.setValue(shutter.getUiName());
			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(propertyValue, stats.get(propertyName));
		}
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty shutter control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testNDFilterControl() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.ND_FILTER.getName();
		String propertyValue;
		controllableProperty.setProperty(propertyName);

		for (NightDayFilter nightDayFilter : NightDayFilter.values()) {
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			propertyValue = nightDayFilter.getUiName();
			controllableProperty.setValue(nightDayFilter.getUiName());
			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(propertyValue, stats.get(propertyName));
		}
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty awb control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testAWBControl() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.AWB.getName();
		String propertyValue;
		controllableProperty.setProperty(propertyName);

		for (AWBMode awbMode : AWBMode.values()) {
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			propertyValue = awbMode.getUiName();
			controllableProperty.setValue(propertyValue);
			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(propertyValue, stats.get(propertyName));
		}

	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty gain control with super gain enable
	 *
	 * Expected: control successfully
	 */
	@Test
	void testGainControl() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.GAIN.getName();
		String propertyValue;
		cameraPanasonicAWUE150Communicator.getMultipleStatistics();
		extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		List<AdvancedControllableProperty> advancedControllablePropertyList = extendedStatistics.getControllableProperties();
		List<String> gainModes = new ArrayList<>();

		// get list of dropdown control value
		for (AdvancedControllableProperty advancedControllableProperty : advancedControllablePropertyList) {
			if (advancedControllableProperty.getName().equals(propertyName)) {
				DropDown dropDown = (DropDown) advancedControllableProperty.getType();
				gainModes = Arrays.asList(dropDown.getLabels());
				break;
			}
		}

		// test all control values
		for (String gain : gainModes) {
			propertyValue = gain;
			controllableProperty.setProperty(propertyName);
			controllableProperty.setValue(propertyValue);
			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(propertyValue, stats.get(propertyName));
		}
	}


	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty iris control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testIrisControl() throws Exception {
		ExtendedStatistics extendedStatistics;
		Map<String, String> stats;
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.IRIS.getName();
		String currentValue = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.IRIS.getName() + ImageAdjustControlMetric.CURRENT_VALUE.getName();
		String propertyValue;

		for (int i = 29; i <= 255; i++) {
			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			propertyValue = String.valueOf(i / 10f);
			controllableProperty.setProperty(propertyName);
			controllableProperty.setValue(propertyValue);
			assertDoesNotThrow(() ->
					cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty)
			);

			cameraPanasonicAWUE150Communicator.getMultipleStatistics();
			extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
			stats = extendedStatistics.getStatistics();
			Assertions.assertEquals(Float.parseFloat(stats.get(currentValue)), DeviceConstant.MAX_IRIS_UI_VALUE - Float.parseFloat(propertyValue) + DeviceConstant.MIN_IRIS_UI_VALUE);
		}
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty iris auto trigger control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testIrisAutoTriggerControl() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.IMAGE_ADJUST.getName() + DeviceConstant.HASH + ImageAdjustControlMetric.IRIS_AUTO_TRIGGER.getName();
		String propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(propertyValue, stats.get(propertyName));
	}

	/**
	 * Test CameraPanasonicAWUE150Communicator.controlProperty iris auto trigger control
	 *
	 * Expected: control successfully
	 */
	@Test
	void testIrisAutoTrigger1Control() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = "Power";
		String propertyValue = "0";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertEquals(propertyValue, stats.get(propertyName));
	}
}
