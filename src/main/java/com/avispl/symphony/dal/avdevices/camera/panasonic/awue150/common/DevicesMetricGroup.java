/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * Set of devices metric group keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public enum DevicesMetricGroup {

	SYSTEM_INFO("SystemInfo"),
	CAMERA_LIVE_INFO("CameraLiveInfo"),
	PAN_TITL_PAD_CONTROL("PanTitlPadControl"),
	ZOOM_CONTROL("ZoomControl"),
	FOCUS_CONTROL("FocusControl"),
	PRESET_CONTROL("PresetControl"),
	IMAGE_ADJUST("ImageAdjust");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of Decoder monitoring metric
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


}
