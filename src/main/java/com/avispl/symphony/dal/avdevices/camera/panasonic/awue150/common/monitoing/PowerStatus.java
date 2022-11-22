/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of power status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum PowerStatus {

	ON("On", "p1", "O1", "1"),
	OFF("Standby", "p0", "O0", "0"),
	ERROR("None", "none", "None", "0");

	private final String uiName;
	private final String apiNameFirst;
	private final String apiNameSecond;
	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of power status
	 * @param apiNameFirst api name first of power status
	 * @param apiNameSecond api name second of power status
	 * @param code code of power status (1/0)
	 */
	PowerStatus(String uiName, String apiNameFirst, String apiNameSecond, String code) {
		this.uiName = uiName;
		this.apiNameFirst = apiNameFirst;
		this.apiNameSecond = apiNameSecond;
		this.code = code;
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
	 * Retrieves {@link #apiNameFirst}
	 *
	 * @return value of {@link #apiNameFirst}
	 */
	public String getApiNameFirst() {
		return apiNameFirst;
	}

	/**
	 * Retrieves {@code {@link #apiNameSecond }}
	 *
	 * @return value of {@link #apiNameSecond}
	 */
	public String getApiNameSecond() {
		return apiNameSecond;
	}

	/**
	 * Retrieves {@link #code}
	 *
	 * @return value of {@link #code}
	 */
	public String getCode() {
		return code;
	}

	/**
	 * This method is used to get power status by API name first
	 *
	 * @param apiValues is the set of live camera info value
	 * @return PowerStatus is the power status that want to get
	 */
	public static PowerStatus getByAPINameFirst(Map<String, String> apiValues) {
		return Arrays.stream(PowerStatus.values()).filter(status -> apiValues.containsKey(status.getApiNameFirst())).findFirst().orElse(PowerStatus.ERROR);
	}

	/**
	 * This method is used to get power status from ui value
	 *
	 * @param code is code of power status
	 * @return PowerStatus is the power status that want to get
	 */
	public static PowerStatus getByCode(String code) {
		return Arrays.stream(PowerStatus.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(PowerStatus.ERROR);
	}

	/**
	 * This method is used to get power status from ui value
	 *
	 * @param apiNameFirst is api name first of power status
	 * @return PowerStatus is the power status that want to get
	 */
	public static PowerStatus getByAPINameFirst(String apiNameFirst) {
		return Arrays.stream(PowerStatus.values()).filter(status -> status.getApiNameFirst().equals(apiNameFirst)).findFirst().orElse(PowerStatus.ERROR);
	}
}

