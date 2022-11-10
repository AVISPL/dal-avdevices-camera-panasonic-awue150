/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of operation lock
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum OperationLock {

	UNLOCK("Unlock", "OSJ:40:0"),
	LOCK("Lock", "OSJ:40:1"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of operation lock
	 * @param apiName api name of operation lock
	 */
	OperationLock(String uiName, String apiName) {
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
	 * This method is used to get operation lock by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return operationLock is the operation lockFan1Status that want to get
	 */
	public static OperationLock getByAPIValue(Map<String, String> apiValues) {
		Optional<OperationLock> operationLock = Arrays.stream(OperationLock.values()).filter(operation -> apiValues.containsKey(operation.getApiName())).findFirst();
		return operationLock.orElse(OperationLock.ERROR);
	}
}

