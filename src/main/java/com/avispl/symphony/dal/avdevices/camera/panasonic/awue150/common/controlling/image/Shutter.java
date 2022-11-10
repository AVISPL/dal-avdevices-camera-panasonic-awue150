/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of shutter modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum Shutter {

	OFF("Off", "OSJ:03:0"),
	STEP("Step", "OSJ:03:1"),
	SYNCHRO("Synchro", "OSJ:03:2"),
	ELC("ELC", "OSJ:03:3");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of shutter status
	 * @param apiName api name shutter status
	 */
	Shutter(String uiName, String apiName) {
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
	 * This method is used to get shutter mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return Shutter is the Shutter status that want to get
	 */
	public static Shutter getByAPIValue(Map<String, String> apiValues) {
		Optional<Shutter> shutter = Arrays.stream(Shutter.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return shutter.orElse(Shutter.OFF);
	}

	/**
	 * This method is used to get Shutter mode from ui value
	 *
	 * @param uiName is ui name of ShutterMode
	 * @return ShutterMode is the ShutterMode status that want to get
	 */
	public static Shutter getByUIName(String uiName) {
		Optional<Shutter> shutter = Arrays.stream(Shutter.values()).filter(status -> status.getUiName().equals(uiName)).findFirst();
		return shutter.orElse(Shutter.OFF);
	}

	/**
	 * This method is used to get Shutter mode from ui value
	 *
	 * @param apiName is api name of ShutterMode
	 * @return ShutterMode is the ShutterMode status that want to get
	 */
	public static Shutter getByAPIValue(String apiName) {
		Optional<Shutter> shutter = Arrays.stream(Shutter.values()).filter(status -> status.getApiName().equals(apiName)).findFirst();
		return shutter.orElse(Shutter.OFF);
	}
}

