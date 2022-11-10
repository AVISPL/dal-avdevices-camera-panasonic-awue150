/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of autofocus status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum AutoFocus {

	MANUAL("Manual", "d10", "OAF:0", "0"),
	AUTO("Auto", "d11","OAF:1", "1"),
	ERROR("None", "None", "None", "-1");

	private final String uiName;
	private final String apiName1;
	private final String apiName2;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of autofocus status
	 * @param apiName1  api name 1 of autofocus status
	 * @param apiName2 api name 2 of autofocus status
	 * @param code code of autofocus status
	 */
	AutoFocus(String uiName, String apiName1, String apiName2, String code) {
		this.uiName = uiName;
		this.apiName1 = apiName1;
		this.apiName2 = apiName2;
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
	 * Retrieves {@code {@link #apiName1 }}
	 *
	 * @return value of {@link #apiName1}
	 */
	public String getApiName1() {
		return apiName1;
	}

	/**
	 * Retrieves {@link #apiName2}
	 *
	 * @return value of {@link #apiName2}
	 */
	public String getApiName2() {
		return apiName2;
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
		Optional<AutoFocus> autoFocus = Arrays.stream(AutoFocus.values()).filter(status -> apiValues.containsKey(status.getApiName1())).findFirst();
		return autoFocus.orElse(AutoFocus.ERROR);
	}

	/**
	 * This method is used to get autofocus status by code values
	 *
	 * @param code is the code of autofocus
	 * @return autoFocus is the autofocus status that want to get
	 */
	public static AutoFocus getByCode(String code) {
		Optional<AutoFocus> autoFocus = Arrays.stream(AutoFocus.values()).filter(status -> status.getCode().equals(code)).findFirst();
		return autoFocus.orElse(AutoFocus.ERROR);
	}

	/**
	 * This method is used to get autofocus status by api name 2 values
	 *
	 * @param apiName2 is the apiName2 of autofocus
	 * @return autoFocus is the autofocus status that want to get
	 */
	public static AutoFocus getByApiName2(String apiName2) {
		Optional<AutoFocus> autoFocus = Arrays.stream(AutoFocus.values()).filter(status -> status.getApiName2().equals(apiName2)).findFirst();
		return autoFocus.orElse(AutoFocus.ERROR);
	}
}

