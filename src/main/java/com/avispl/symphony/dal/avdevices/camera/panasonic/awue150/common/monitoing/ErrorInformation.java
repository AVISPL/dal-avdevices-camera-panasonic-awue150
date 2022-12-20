/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of fan status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum ErrorInformation {

	NO_ERROR("No Error", "OSI:46:0x00000000"),
	FAN_ERROR("Fan Error", "OSI:46:0x00000001"),
	HIGH_TEMPERATURE("High Temperature", "OSI:46:0x00000002"),
	LENS_ERROR("Lens Error", "OSI:46:0x00000004"),
	PAN_TILT_ERROR("Pan Tilt Error", "OSI:46:0x00000008"),
	SENSOR_ERROR("Sensor Error", "OSI:46:0x00000010"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of error information
	 * @param apiName api name of error information
	 */
	ErrorInformation(String uiName, String apiName) {
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
	 * @return fanStatus is the fan status that want to get
	 */
	public static ErrorInformation getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(ErrorInformation.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(ErrorInformation.ERROR);
	}
}

