/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of Iris modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum IrisMode {

	MANUAL("Manual", "d30", "0"),
	AUTO("Auto", "d31", "1"),
	ERROR("None", "None", "0");

	private final String uiName;
	private final String apiName;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of iris mode status
	 * @param apiName api name iris mode status
	 * @param code code of autofocus status
	 */
	IrisMode(String uiName, String apiName, String code) {
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
	 * This method is used to get iris mode from api value
	 *
	 * @param apiValues is the set of live camera info value
	 * @return IrisMode is the IrisMode status that want to get
	 */
	public static IrisMode getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(IrisMode.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(IrisMode.ERROR);
	}

	/**
	 * This method is used to get autofocus status by code value
	 *
	 * @param code is the code of iris mode
	 * @return iris is the iris mode status that want to get
	 */
	public static IrisMode getByCode(String code) {
		return Arrays.stream(IrisMode.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(IrisMode.ERROR);
	}

	/**
	 * This method is used to get autofocus status by code value
	 *
	 * @param uiName is the ui name of iris mode
	 * @return iris is the iris mode status that want to get
	 */
	public static IrisMode getByUiName(String uiName) {
		return Arrays.stream(IrisMode.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(IrisMode.ERROR);
	}
}

