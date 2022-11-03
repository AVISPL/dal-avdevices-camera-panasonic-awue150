/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of power on position status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum PowerOnPosition {

	NONE("None", "OSJ:45:0"),
	STANDBY("Standby", "OSJ:45:1"),
	HOME("Home", "OSJ:45:2"),
	PRESET("Preset", "OSJ:45:3");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of power on position
	 * @param apiName  api name power on position
	 */
	PowerOnPosition(String uiName, String apiName) {
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
	 * This method is used to get power on position status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return powerOnPosition is the power on position status that want to get
	 */
	public static PowerOnPosition getByAPIValue(Map<String, String> apiValues) {
		Optional<PowerOnPosition> powerOnPosition = Arrays.stream(PowerOnPosition.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return powerOnPosition.orElse(PowerOnPosition.NONE);
	}
}

