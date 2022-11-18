/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of AWB modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum AWBMode {

	ATW("ATW", "OAW:0", "OAW:0"),
	AWB_A("AWB A", "OAW:2", "OAW:1"),
	AWB_B("AWB B", "OAW:3", "OAW:2"),
	TEMPERATURE_3200K("3200K", "OAW:4", "OAW:4"),
	TEMPERATURE_5600K("5600K", "OAW:5", "OAW:5"),
	AUTO("VAR", "OAW:9", "OAW:9");

	private final String uiName;
	private final String apiNameFirst;
	private final String apiNameSecond;

	/**
	 * Parameterized constructor
	 *  @param uiName ui name of AWB mode status
	 * @param apiNameFirst api name AWB mode status
	 * @param apiNameSecond
	 */
	AWBMode(String uiName, String apiNameFirst, String apiNameSecond) {
		this.uiName = uiName;
		this.apiNameFirst = apiNameFirst;
		this.apiNameSecond = apiNameSecond;
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
	 * Retrieves {@code {@link #apiNameFirst }}
	 *
	 * @return value of {@link #apiNameFirst}
	 */
	public String getApiNameFirst() {
		return apiNameFirst;
	}

	/**
	 * Retrieves {@link #apiNameSecond}
	 *
	 * @return value of {@link #apiNameSecond}
	 */
	public String getApiNameSecond() {
		return apiNameSecond;
	}

	/**
	 * This method is used to get AWB mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBMode getByAPINameFirst(Map<String, String> apiValues) {
		return Arrays.stream(AWBMode.values()).filter(status -> apiValues.containsKey(status.getApiNameFirst())).findFirst().orElse(AWBMode.AUTO);
	}

	/**
	 * This method is used to get AWB mode from ui value
	 *
	 * @param uiName is ui name of AWBMode
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBMode getByUIName(String uiName) {
		return Arrays.stream(AWBMode.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(AWBMode.AUTO);
	}

	/**
	 * This method is used to get AWB mode from api values
	 *
	 * @param apiName is api name of AWBMode
	 * @return AWBMode is the AWBMode status that want to get
	 */
	public static AWBMode getByAPINameSecond(String apiName) {
		return Arrays.stream(AWBMode.values()).filter(status -> status.getApiNameSecond().equals(apiName)).findFirst().orElse(AWBMode.AUTO);
	}
}

