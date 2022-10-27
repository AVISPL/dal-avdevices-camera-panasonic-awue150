/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of fan status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 5/4/2022
 * @since 1.0.0
 */
public enum ErrorInformation {

	NO_ERROR("NoError", "OSI:46:0x00000000"),
	FAN_ERROR("FanError", "OSI:46:0x00000001"),
	HIGH_TEMPERATURE("HighTemperature", "OSI:46:0x00000002"),
	LENS_ERROR("LensError", "OSI:46:0x00000004"),
	PAN_TILT_ERROR("PanTiltError", "OSI:46:0x00000008"),
	SENSOR_ERROR("SensorError", "OSI:46:0x00000010"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of decoder buffering mode
	 * @param apiName  api name of decoder buffering mode
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
		Optional<ErrorInformation> fan1Status = Arrays.stream(ErrorInformation.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return fan1Status.orElse(ErrorInformation.ERROR);
	}
}

