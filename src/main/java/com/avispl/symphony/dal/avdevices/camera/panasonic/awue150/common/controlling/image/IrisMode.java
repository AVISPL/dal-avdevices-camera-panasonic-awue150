/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of Iris modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum IrisMode {

	MANUAL("", "d30"),
	AUTO("", "d31"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of iris mode status
	 * @param apiName api name iris mode status
	 */
	IrisMode(String uiName, String apiName) {
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
	 * This method is used to get iris mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return IrisMode is the IrisMode status that want to get
	 */
	public static IrisMode getByAPIValue(Map<String, String> apiValues) {
		Optional<IrisMode> irisMode = Arrays.stream(IrisMode.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return irisMode.orElse(IrisMode.ERROR);
	}
}

