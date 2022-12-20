/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of error status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum ErrorStatusInformation {

	NO_ERROR("No Error", "rER00"),
	ERROR_03("Motor Driver Error", "rER03"),
	ERROR_04("Pan Sensor Error", "rER04"),
	ERROR_05("Tilt Sensor Error", "rER05"),
	ERROR_06("Controller RX Over run Error", "rER06"),
	ERROR_07("Controller RX Framing Error", "rER07"),
	ERROR_08("Network RX Over run Error", "rER08"),
	ERROR_09("Network RX Framing Error", "rER09"),
	ERROR_17("Controller RX Command Buffer Overflow", "rER17"),
	ERROR_19("Network RX Command Buffer Overflow", "rER19"),
	ERROR_21("System Error", "rER21"),
	ERROR_22("Spec Limit Over", "rER22"),
	ERROR_23("FPGA Config Error", "rER23"),
	ERROR_24("NET Life-monitoring Error", "rER24"),
	ERROR_25("BE Life-monitoring Error", "rER25"),
	ERROR_26("IF/BE UART Buffer Overflow", "rER26"),
	ERROR_27("IF/BE UART Framing Error", "rER27"),
	ERROR_28("IF/BE UART Buffer Overflow", "rER28"),
	ERROR_29("CAM Life-monitoring Error", "rER29"),
	ERROR_30("Error30", "rER30"),
	ERROR_31("Fan1 error", "rER31"),
	ERROR_32("Fan2 error", "rER32"),
	ERROR_33("High Temp", "rER33"),
	ERROR_40("Temp Sensor Error", "rER40"),
	ERROR_41("Lens Initialize Error", "rER41"),
	ERROR_42("PT. Initialize Error", "rER42"),
	ERROR_50("MR Level Error", "rER50"),
	ERROR_51("Error51", "rER51"),
	ERROR_52("MR Offset Error", "rER52"),
	ERROR_53("Origin Offset Error", "rER53"),
	ERROR_54("Angle MR Sensor Error", "rER54"),
	ERROR_55("PT. Gear Error", "rER55"),
	ERROR_56("Motor Disconnect Error", "rER56"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of error status
	 * @param apiName api name of error status
	 */
	ErrorStatusInformation(String uiName, String apiName) {
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
	 * This method is used to get error status by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return errorStatusInformation is the error status that want to get
	 */
	public static ErrorStatusInformation getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(ErrorStatusInformation.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst().orElse(ErrorStatusInformation.ERROR);
	}
}

