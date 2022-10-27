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
public enum PowerStatus {

	ON("On", "p1"),
	OFF("Off", "p0"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of decoder buffering mode
	 * @param apiName  api name of decoder buffering mode
	 */
	PowerStatus(String uiName, String apiName) {
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
	 * @return powerStatus is the power status that want to get
	 */
	public static PowerStatus getByAPIValue(Map<String, String> apiValues) {
		Optional<PowerStatus> powerStatus = Arrays.stream(PowerStatus.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return powerStatus.orElse(PowerStatus.ERROR);
	}
}

