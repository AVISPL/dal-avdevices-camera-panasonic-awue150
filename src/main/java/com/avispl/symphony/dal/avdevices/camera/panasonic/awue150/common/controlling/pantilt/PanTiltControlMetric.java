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
	HOME("(00)Home"),
	UP("(01)Up"),
	DOWN("(02)Down"),
	LEFT("(03)Left"),
	RIGHT("(04)Right"),
	UP_LEFT("(05)UpLeft"),
	UP_RIGHT("(06)UpRight"),
	DOWN_LEFT("(07)DownLeft"),
	DOWN_RIGHT("(08)DownRight"),
	POWER_ON_POSITION("(09)PowerOnPosition"),
	PT_ABSOLUTE_POSITION_CONTROL("(10)PTAbsolutePositionControl"),
	PT_SPEED("(11)PTSpeed"),
	PT_SPEED_CURRENT_VALUE("(12)PTSpeedCurRentValue"),
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

