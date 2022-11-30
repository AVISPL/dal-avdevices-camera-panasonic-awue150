/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.preset;

import java.util.Arrays;

/**
 * Set of focus control metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum PresetControlMetric {

	// Static metric
	HOME("Home"),
	LAST_PRESET("LastPreset"),
	PRESET("Preset"),
	APPLY_PRESET("RecallPreset"),
	DELETE_PRESET("DeletePreset"),
	SET_PRESET("SetPreset");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of controlling metric
	 */
	PresetControlMetric(String name) {
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
	 * This method is used to get preset control metric by name
	 *
	 * @param name is the name of preset control metric that want to get
	 * @return PresetControlMetric is the preset control metric that want to get
	 */
	public static PresetControlMetric getByName(String name) {
		return Arrays.stream(PresetControlMetric.values()).filter(control -> control.getName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("Focus control %s is not supported.", name)));
	}
}

