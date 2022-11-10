/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt;

import java.util.Arrays;
import java.util.Optional;

/**
 * Set of focus control metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum PanTiltControlMetric {

	// Static metric
	HOME("00Home"),
	UP("01Up"),
	DOWN("02Down"),
	LEFT("03Left"),
	RIGHT("04Right"),
	UP_LEFT("05UpLeft"),
	UP_RIGHT("06UpRight"),
	DOWN_LEFT("07DownLeft"),
	DOWN_RIGHT("08DownRight"),
	POWER_ON_POSITION("09PowerOnPosition"),
	PT_ABSOLUTE_POSITION_CONTROL("10PTAbsolutePositionControl"),
	PT_SPEED("11PTSpeed"),
	PT_SPEED_CURRENT_VALUE("12PTSpeedCurrentValue"),
	PAN("Pan"),
	TILT("Tilt");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of controlling metric
	 */
	PanTiltControlMetric(String name) {
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
	public static PanTiltControlMetric getByName(String name) {
		Optional<PanTiltControlMetric> presetControlMetric = Arrays.stream(PanTiltControlMetric.values()).filter(control -> control.getName().equals(name)).findFirst();
		if (presetControlMetric.isPresent()) {
			return presetControlMetric.get();
		}else {
			throw new IllegalStateException(String.format("Focus control %s is not supported.", name));
		}
	}

}

