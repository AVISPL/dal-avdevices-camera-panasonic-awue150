/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of focus ADJ with PTZ status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum FocusADJWithPTZ {

	MANUAL("Off", "OAZ:0", "0"),
	AUTO("On", "OAZ:1", "1"),
	ERROR("None", "None", "-1");

	private final String uiName;
	private final String apiName;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of focus ADJ with PTZ status
	 * @param apiName api name focus ADJ with PTZ status
	 * @param code code af focus ADJ with PTZ status (0/1)
	 */
	FocusADJWithPTZ(String uiName, String apiName, String code) {
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
	 * This method is used to get fan status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return focusADJWithPTZ is the focus ADJ with PTZ status that want to get
	 */
	public static FocusADJWithPTZ getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(FocusADJWithPTZ.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(FocusADJWithPTZ.ERROR);
	}

	/**
	 * This method is used to get focus ADJ with PTZ status by API values
	 *
	 * @param apiValue is the api value of focus ADJ with PTZ
	 * @return focusADJWithPTZ is the focus ADJ with PTZ status that want to get
	 */
	public static FocusADJWithPTZ getByAPIValue(String apiValue) {
		return Arrays.stream(FocusADJWithPTZ.values()).filter(status -> apiValue.equals(status.getApiName())).findFirst().orElse(FocusADJWithPTZ.ERROR);
	}

	/**
	 * This method is used to get focus ADJ with PTZ status by API values
	 *
	 * @param code is the code of focus ADJ with PTZ
	 * @return focusADJWithPTZ is the focus ADJ with PTZ status that want to get
	 */
	public static FocusADJWithPTZ getByAPICode(String code) {
		return Arrays.stream(FocusADJWithPTZ.values()).filter(status -> code.equals(status.getCode())).findFirst().orElse(FocusADJWithPTZ.ERROR);
	}
}

