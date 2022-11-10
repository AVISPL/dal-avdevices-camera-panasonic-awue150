/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;

/**
 * Set of AWB color temperature status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum AWBColorTemperature {

	VALID("Valid", "O"),
	UNDER("Under", "1"),
	OVER("Over", "2");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of AWB mode status
	 * @param apiName api name AWB mode status
	 */
	AWBColorTemperature(String uiName, String apiName) {
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
	 * This method is used to get AWB color temperature from api value
	 *
	 * @param apiName is api name of AWB color temperature
	 * @return AWBColorTemperature is the AWB Color Temperature status that want to get
	 */
	public static AWBColorTemperature getByAPIName(String apiName) {
		return Arrays.stream(AWBColorTemperature.values()).filter(status -> status.getApiName().equals(apiName)).findFirst().orElse(AWBColorTemperature.VALID);
	}
}

