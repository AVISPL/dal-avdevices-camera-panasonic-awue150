/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.zoom;

import java.util.Arrays;

/**
 * Set of zoom control metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum ZoomControlMetric {

	// Static metric
	ZOOM_CONTROL_SPEED("ZoomControlSpeed"),
	ZOOM_CONTROL("ZoomControl"),
	ZOOM_OUT("ZoomOut"),
	ZOOM_IN("ZoomIn"),
	CURRENT_VALUE("CurrentValue"),
	WIDE("-"),
	TELE("+");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of controlling metric
	 */
	ZoomControlMetric(String name) {
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
	 * This method is used to get focus control metric by name
	 *
	 * @param name is the name of focus control that want to get
	 * @return FocusControlMetric is the focus control that want to get
	 */
	public static ZoomControlMetric getByName(String name) {
		return Arrays.stream(ZoomControlMetric.values()).filter(control -> control.getName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Focus control %s is not supported.", name)));
	}
}

