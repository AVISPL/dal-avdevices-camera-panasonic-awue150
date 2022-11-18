/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of night day modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum NightDayMode {

	NIGHT("Night", "d61"),
	DAY("Day", "d60");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of night/day mode status
	 * @param apiName api name night/day mode status
	 */
	NightDayMode(String uiName, String apiName) {
		this.uiName = uiName;
		this.apiName = apiName;
	}

	/**
	 * Retrieves {@code {@link #uiName }}
	 *
	 * @return value of {@link #uiName}
	 */
	public String getUiName() {
		return this.uiName;
	}

	/**
	 * Retrieves {@code {@link #apiName}}
	 *
	 * @return value of {@link #apiName}
	 */
	public String getApiName() {
		return apiName;
	}

	/**
	 * This method is used to get night/day mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return SuperGain is the night/day status that want to get
	 */
	public static NightDayMode getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(NightDayMode.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(NightDayMode.DAY);
	}
}

