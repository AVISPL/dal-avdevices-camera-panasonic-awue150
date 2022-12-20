/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of autofocus status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum AutoFocus {

	MANUAL("Manual", "d10", "OAF:0", "0"),
	AUTO("Auto", "d11", "OAF:1", "1"),
	ERROR("None", "None", "None", "-1");

	private final String uiName;
	private final String apiNameFirst;
	private final String apiNameSecond;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of autofocus status
	 * @param apiNameFirst api name 1 of autofocus status
	 * @param apiNameSecond api name 2 of autofocus status
	 * @param code code of autofocus status
	 */
	AutoFocus(String uiName, String apiNameFirst, String apiNameSecond, String code) {
		this.uiName = uiName;
		this.apiNameFirst = apiNameFirst;
		this.apiNameSecond = apiNameSecond;
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
	 * Retrieves {@code {@link #apiNameFirst }}
	 *
	 * @return value of {@link #apiNameFirst}
	 */
	public String getApiNameFirst() {
		return apiNameFirst;
	}

	/**
	 * Retrieves {@link #apiNameSecond}
	 *
	 * @return value of {@link #apiNameSecond}
	 */
	public String getApiNameSecond() {
		return apiNameSecond;
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
	 * This method is used to get autofocus status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return autoFocus is the autofocus status that want to get
	 */
	public static AutoFocus getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(AutoFocus.values()).filter(status -> apiValues.containsKey(status.getApiNameFirst())).findFirst().orElse(AutoFocus.ERROR);
	}

	/**
	 * This method is used to get autofocus status by code values
	 *
	 * @param code is the code of autofocus
	 * @return autoFocus is the autofocus status that want to get
	 */
	public static AutoFocus getByCode(String code) {
		return Arrays.stream(AutoFocus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(AutoFocus.ERROR);
	}

	/**
	 * This method is used to get autofocus status by api name 2 values
	 *
	 * @param apiName2 is the apiName2 of autofocus
	 * @return autoFocus is the autofocus status that want to get
	 */
	public static AutoFocus getByApiName2(String apiName2) {
		return Arrays.stream(AutoFocus.values()).filter(status -> status.getApiNameSecond().equals(apiName2)).findFirst().orElse(AutoFocus.ERROR);
	}
}

