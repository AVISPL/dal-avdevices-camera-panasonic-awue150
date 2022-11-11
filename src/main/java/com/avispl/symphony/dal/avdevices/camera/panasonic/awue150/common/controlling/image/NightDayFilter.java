/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of night day filter modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum NightDayFilter {

	THROUGH("Through", "OFT:0"),
	ND_1_4("1/4", "OFT:1"),
	ND_1_16("1/16", "OFT:2"),
	ND_1_64("1/64", "OFT:3");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of night day filter status
	 * @param apiName api name night day filter status
	 */
	NightDayFilter(String uiName, String apiName) {
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
	 * This method is used to get night day filter mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return NightDayFilter is the night day filter status that want to get
	 */
	public static NightDayFilter getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(NightDayFilter.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(THROUGH);

	}

	/**
	 * This method is used to get ND filter mode from ui value
	 *
	 * @param uiName is ui name of ND filter mode
	 * @return NightDayFilter is the ND filter status that want to get
	 */
	public static NightDayFilter getByUIName(String uiName) {
		return Arrays.stream(NightDayFilter.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(THROUGH);
	}

	/**
	 * This method is used to get ND filter mode from ui value
	 *
	 * @param apiName is api name of ND filter mode
	 * @return NightDayFilter is the ND filter status that want to get
	 */
	public static NightDayFilter getByAPIName(String apiName) {
		return Arrays.stream(NightDayFilter.values()).filter(status -> status.getApiName().equals(apiName)).findFirst().orElse(THROUGH);

	}
}

