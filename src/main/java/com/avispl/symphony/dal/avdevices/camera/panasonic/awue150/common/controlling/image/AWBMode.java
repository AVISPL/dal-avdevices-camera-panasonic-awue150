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
public enum AWBMode {

	ATW("ATW", "OAW:0"),
	AWB_A("AWB A", "OAW:2"),
	AWB_B("AWB b", "OAW:3"),
	TEMPERATURE_3200K("3200K", "OAW:4"),
	TEMPERATURE_5600K("5600K", "OAW:5"),
	AUTO("VAR", "OAW:9"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of AWB mode status
	 * @param apiName api name AWB mode status
	 */
	AWBMode(String uiName, String apiName) {
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
	public static AWBMode getByAPIValue(Map<String, String> apiValues) {
		Optional<AWBMode> awbMode = Arrays.stream(AWBMode.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return awbMode.orElse(AWBMode.ERROR);
	}
}

