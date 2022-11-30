/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of UHD crop status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum UHDCrop {

	OFF("Off", "OSJ:2E:0x0"),
	CROP_1080("Crop(1080)", "OSJ:2E:0x1"),
	CROP_720("Crop(720)", "OSJ:2E:0x2"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of fan status
	 * @param apiName api name of fan status
	 */
	UHDCrop(String uiName, String apiName) {
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
	 * This method is used to get UHD crop status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return UHDCrop is the UHD crop status that want to get
	 */
	public static UHDCrop getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(UHDCrop.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(UHDCrop.ERROR);
	}
}

