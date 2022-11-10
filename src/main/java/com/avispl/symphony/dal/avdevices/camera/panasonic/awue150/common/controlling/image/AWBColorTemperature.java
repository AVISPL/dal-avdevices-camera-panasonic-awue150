/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of AWB modes
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
	 * This method is used to get AWB mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBColorTemperature getByAPIValue(Map<String, String> apiValues) {
		Optional<AWBColorTemperature> awbMode = Arrays.stream(AWBColorTemperature.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return awbMode.orElse(AWBColorTemperature.VALID);
	}

	/**
	 * This method is used to get AWB mode from ui value
	 *
	 * @param uiName is ui name of AWBMode
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBColorTemperature getByUIName(String uiName) {
		Optional<AWBColorTemperature> awbMode = Arrays.stream(AWBColorTemperature.values()).filter(status -> status.getUiName().equals(uiName)).findFirst();
		return awbMode.orElse(AWBColorTemperature.VALID);
	}

	/**
	 * This method is used to get AWB mode from api values
	 *
	 * @param apiName is api name of AWBMode
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBColorTemperature getByAPIName(String apiName) {
		Optional<AWBColorTemperature> awbMode = Arrays.stream(AWBColorTemperature.values()).filter(status -> status.getApiName().equals(apiName)).findFirst();
		return awbMode.orElse(AWBColorTemperature.VALID);
	}
}

