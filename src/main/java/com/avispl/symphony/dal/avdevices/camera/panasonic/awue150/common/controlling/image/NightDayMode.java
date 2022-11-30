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

	NIGHT("Night", "d61", "1"),
	DAY("Day", "d60", "0");

	private final String uiName;
	private final String apiName;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of night/day mode status
	 * @param apiName api name night/day mode status
	 * @param code code of night/day mode
	 */
	NightDayMode(String uiName, String apiName, String code) {
		this.uiName = uiName;
		this.apiName = apiName;
		this.code = code;
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
	 * Retrieves {@link #code}
	 *
	 * @return value of {@link #code}
	 */
	public String getCode() {
		return code;
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

	/**
	 * This method is used to get focus ADJ with PTZ status by API values
	 *
	 * @param apiValue is the api value of night day mode
	 * @return NightDayMode is the night day mode status that want to get
	 */
	public static NightDayMode getByAPIValue(String apiValue) {
		return Arrays.stream(NightDayMode.values()).filter(status -> apiValue.equals(status.getApiName())).findFirst().orElse(NightDayMode.DAY);
	}

	/**
	 * This method is used to get focus ADJ with PTZ status by API values
	 *
	 * @param code is the code of night day mode
	 * @return NightDayMode is the night day mode status that want to get
	 */
	public static NightDayMode getByCode(String code) {
		return Arrays.stream(NightDayMode.values()).filter(status -> code.equals(status.getCode())).findFirst().orElse(NightDayMode.DAY);
	}
}

