/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of operation lock
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum OperationLock {

	UNLOCK("Unlock", "OSJ:40:0", "OSJ:3F", "0"),
	LOCK("Lock", "OSJ:40:1", "OSJ:3E:0123456789012345678901234567890123456789&ope=ABCDE&res=1", "1"),
	ERROR("None", "None", "none", "-1");

	private final String uiName;
	private final String apiNameFirst;
	private final String apiNameSecond;

	private final String code;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of operation lock
	 * @param apiNameFirst api name first of operation lock
	 * @param apiNameSecond api name second of operation lock
	 * @param code code of operation lock status (1/0)
	 */
	OperationLock(String uiName, String apiNameFirst, String apiNameSecond, String code) {
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
	 * Retrieves {@link #code}
	 *
	 * @return value of {@link #code}
	 */
	public String getCode() {
		return code;
	}

	/**
	 * This method is used to get operation lock by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return operationLock is the operation lockFan1Status that want to get
	 */
	public static OperationLock getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(OperationLock.values()).filter(operation -> apiValues.containsKey(operation.getApiNameFirst())).findFirst().orElse(OperationLock.ERROR);
	}

	/**
	 * This method is used to get operation lock of status from code value
	 *
	 * @param code is code of operation lock
	 * @return PowerStatus is the operation lock status that want to get
	 */
	public static OperationLock getByCode(String code) {
		return Arrays.stream(OperationLock.values()).filter(status -> status.getCode().equals(code)).findFirst().orElse(OperationLock.ERROR);
	}

}

