/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of super gain modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum SuperGain {

	ON("On", "OSI:28:1"),
	OFF("Off", "OSI:28:0");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of super gain mode status
	 * @param apiName api name super mode status
	 */
	SuperGain(String uiName, String apiName) {
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
	 * This method is used to get super gain mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return SuperGain is the super gain status that want to get
	 */
	public static SuperGain getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(SuperGain.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(SuperGain.OFF);
	}

	/**
	 * This method is used to get SuperGain mode from ui value
	 *
	 * @param uiName is ui name of SuperGain
	 * @return SuperGain is the super gain status that want to get
	 */
	public static SuperGain getByUIName(String uiName) {
		return Arrays.stream(SuperGain.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(SuperGain.OFF);
	}

	/**
	 * This method is used to get SuperGain mode from api values
	 *
	 * @param apiName is api name of SuperGain
	 * @return SuperGain is the super gain status that want to get
	 */
	public static SuperGain getByAPIValue(String apiName) {
		return Arrays.stream(SuperGain.values()).filter(status -> status.getApiName().equals(apiName)).findFirst().orElse(SuperGain.OFF);
	}
}

