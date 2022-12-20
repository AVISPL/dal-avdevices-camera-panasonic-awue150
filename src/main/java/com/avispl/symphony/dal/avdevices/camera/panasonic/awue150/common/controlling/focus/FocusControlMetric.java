/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus;

import java.util.Arrays;
import java.util.Optional;

/**
 * Set of focus control metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum FocusControlMetric {

	// Static metric
	AUTO_FOCUS("AutoFocus"),
	FOCUS_ADJ_WITH_PTZ("FocusADJWithPTZ"),
	FOCUS_CONTROL_SPEED("FocusControlSpeed"),
	FOCUS_CONTROL("FocusControl"),
	FOCUS_CONTROL_NEAR("FocusControlNear"),
	FOCUS_CONTROL_FAR("FocusControlFar"),
	CURRENT_VALUE("CurrentValue"),
	NEAR("Near"),
	FAR("Far"),
	FOCUS_MODE("FocusMode");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of controlling metric
	 */
	FocusControlMetric(String name) {
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
	public static FocusControlMetric getByName(String name) {
		Optional<FocusControlMetric> focusControlMetric = Arrays.stream(FocusControlMetric.values()).filter(control -> control.getName().equals(name)).findFirst();
		if (focusControlMetric.isPresent()) {
			return focusControlMetric.get();
		}else {
			throw new IllegalStateException(String.format("Focus control %s is not supported.", name));
		}
	}

}

