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
public enum Fan1Status {

	AUTO("Auto", "fAN0"),
	HIGH("High", "fAN1"),
	MID("Mid", "fAN2"),
	LOW("Low", "fAN3"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of decoder buffering mode
	 * @param apiName  api name of decoder buffering mode
	 */
	Fan1Status(String uiName, String apiName) {
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
	public static Fan1Status getByAPIValue(Map<String, String> apiValues) {
		Optional<Fan1Status> fan1Status = Arrays.stream(Fan1Status.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return fan1Status.orElse(Fan1Status.ERROR);
	}
}

