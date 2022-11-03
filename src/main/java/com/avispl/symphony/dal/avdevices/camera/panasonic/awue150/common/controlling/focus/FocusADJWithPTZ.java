/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of focus ADJ with PTZ status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum FocusADJWithPTZ {

	MANUAL("Off", "OAZ:0"),
	AUTO("On", "OAZ:1"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of focus ADJ with PTZ status
	 * @param apiName  api name focus ADJ with PTZ status
	 */
	FocusADJWithPTZ(String uiName, String apiName) {
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
	 * This method is used to get fan status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return focusADJWithPTZ is the focus ADJ with PTZ status that want to get
	 */
	public static FocusADJWithPTZ getByAPIValue(Map<String, String> apiValues) {
		Optional<FocusADJWithPTZ> focusADJWithPTZ = Arrays.stream(FocusADJWithPTZ.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return focusADJWithPTZ.orElse(FocusADJWithPTZ.ERROR);
	}
}

