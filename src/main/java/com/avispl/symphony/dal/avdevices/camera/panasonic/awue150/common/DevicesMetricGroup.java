/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

import java.util.Arrays;
import java.util.Optional;

/**
 * Set of devices metric group keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum DevicesMetricGroup {

	SYSTEM_INFO("SystemInfo"),
	CAMERA_LIVE_INFO("CameraLiveInfo"),
	MODEL_NAME("ModelName"),
	SIMULTANEOUS("Simultaneous"),
	PAN_TILT_PAD_CONTROL("PanTiltPadControl"),
	ZOOM_CONTROL("ZoomControl"),
	FOCUS_CONTROL("FocusControl"),
	PRESET_CONTROL("PresetControl"),
	IMAGE_ADJUST("ImageAdjust");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of monitoring metric group
	 */
	DevicesMetricGroup(String name) {
		this.name = name;

	}

	/**
	 * retrieve {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method is used to get device metric group by name
	 *
	 * @param name is the name of focus control that want to get
	 * @return DevicesMetricGroup is the focus control that want to get
	 */
	public static DevicesMetricGroup getByName(String name) {
		Optional<DevicesMetricGroup> devicesMetricGroup = Arrays.stream(DevicesMetricGroup.values()).filter(group -> group.getName().equals(name)).findFirst();
		if (devicesMetricGroup.isPresent()) {
			return devicesMetricGroup.get();
		} else {
			throw new IllegalStateException(String.format("control group %s is not supported.", name));
		}
	}
}
