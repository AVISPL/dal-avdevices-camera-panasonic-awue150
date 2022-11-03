/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */

import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.CameraPanasonicAWUE150Communicator;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceConstant;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DeviceInfoMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.DevicesMetricGroup;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.FocusControlMetric;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.preset.PresetControlMetric;

/**
 * Unit test for CameraPanasonicAWUE150Communicator
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 26/10/2022
 * @version 1.0
 * @since 1.0
 */
class CameraPanasonicAWUE150CommunicatorTest {
	private final CameraPanasonicAWUE150Communicator cameraPanasonicAWUE150Communicator = new CameraPanasonicAWUE150Communicator();

	@BeforeEach()
	public void setUp() throws Exception {
		cameraPanasonicAWUE150Communicator.setHost("***REMOVED***");
		cameraPanasonicAWUE150Communicator.setPort(443);
		cameraPanasonicAWUE150Communicator.setLogin("admin");
		cameraPanasonicAWUE150Communicator.setPassword("admin");
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
	@Tag("RealDevice")
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
	@Tag("RealDevice")
	@Test
	void testHaivisionX4DecoderCommunicatorGetMonitoringDataFailed() throws Exception {
		CameraPanasonicAWUE150Communicator cameraPanasonicAWUE150CommunicatorSpy = Mockito.spy(CameraPanasonicAWUE150Communicator.class);
		cameraPanasonicAWUE150CommunicatorSpy.setHost("***REMOVED***");
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
	 * Test CameraPanasonicAWUE150Communicator.controlProperty focus control: push focus far
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
	 * Test SamSungSmartThingsAggregator.controlProperty location management : Change location name
	 *
	 * Expected: control successfully
	 */
	@Test
	void testSetPreset() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = "Preset001";
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
	 * Test SamSungSmartThingsAggregator.controlProperty location management : Change location name
	 *
	 * Expected: control successfully
	 */
	@Test
	void testApplyPreset() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = "Preset001";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.APPLY_PRESET.getName();
		propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
		String lastPreset = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.LAST_PRESET.getName();
		Assertions.assertEquals("Preset001", stats.get(lastPreset));
	}

	/**
	 * Test SamSungSmartThingsAggregator.controlProperty location management : Change location name
	 *
	 * Expected: control successfully
	 */
	@Test
	void testPanTiltUp() throws Exception {
		ExtendedStatistics extendedStatistics = (ExtendedStatistics) cameraPanasonicAWUE150Communicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistics.getStatistics();
		ControllableProperty controllableProperty = new ControllableProperty();

		String propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.PRESET.getName();
		String propertyValue = "Preset001";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		propertyName = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.APPLY_PRESET.getName();
		propertyValue = "1";
		controllableProperty.setProperty(propertyName);
		controllableProperty.setValue(propertyValue);
		cameraPanasonicAWUE150Communicator.controlProperty(controllableProperty);

		Assertions.assertNull(stats.get(propertyName));
		String lastPreset = DevicesMetricGroup.PRESET_CONTROL.getName() + DeviceConstant.HASH + PresetControlMetric.LAST_PRESET.getName();
		Assertions.assertEquals("Preset001", stats.get(lastPreset));
	}
}
