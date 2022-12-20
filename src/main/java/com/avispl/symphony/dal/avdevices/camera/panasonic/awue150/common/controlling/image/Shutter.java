/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of shutter modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum Shutter {

	OFF("Off", "OSJ:03:0x0", "OSJ:03:0"),
	STEP("Step", "OSJ:03:0x1", "OSJ:03:1"),
	SYNCHRO("Synchro", "OSJ:03:0x2", "OSJ:03:2"),
	ELC("ELC", "OSJ:03:0x3", "OSJ:03:3");

	private final String uiName;
	private final String apiNameFirst;
	private final String apiNameSecond;

	/**
	 * Parameterized constructor
	 *  @param uiName ui name of shutter status
	 * @param apiNameFirst api name first of shutter status
	 * @param apiNameSecond api name second of shutter status
	 */
	Shutter(String uiName, String apiNameFirst, String apiNameSecond) {
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
	 * This method is used to get shutter mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return Shutter is the Shutter status that want to get
	 */
	public static Shutter getByAPINameFirst(Map<String, String> apiValues) {
		return Arrays.stream(Shutter.values()).filter(status -> apiValues.containsKey(status.getApiNameFirst())).findFirst().orElse(Shutter.OFF);
	}

	/**
	 * This method is used to get Shutter mode from ui value
	 *
	 * @param uiName is ui name of ShutterMode
	 * @return ShutterMode is the ShutterMode status that want to get
	 */
	public static Shutter getByUIName(String uiName) {
		return Arrays.stream(Shutter.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(Shutter.OFF);
	}

	/**
	 * This method is used to get Shutter mode from ui value
	 *
	 * @param apiNameSecond is api name second of ShutterMode
	 * @return ShutterMode is the ShutterMode status that want to get
	 */
	public static Shutter getByAPINameSecond(String apiNameSecond) {
		return Arrays.stream(Shutter.values()).filter(status -> status.getApiNameSecond().equals(apiNameSecond)).findFirst().orElse(Shutter.OFF);
	}
}

