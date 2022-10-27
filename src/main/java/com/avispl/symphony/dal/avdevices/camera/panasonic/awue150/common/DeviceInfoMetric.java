/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * Set of device info metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum DeviceInfoMetric {

	// Static metric
	MAC_ADDRESS("MacAddress"),
	SERIAL_NUMBER("SerialNumber"),
	FIRMWARE_VERSION("FirmwareVersion"),
	MODEL_NAME("ModelName"),
	OUTPUT_FORMAT("OutputFormat"),
	OPERATION_LOCK("OperationLock"),
	CAMERA_TITLE("CameraTitle"),
	FAN1_STATUS("Fan1Status"),
	FAN2_STATUS("Fan2Status"),
	ERROR_INFORMATION("ErrorInformation"),
	ERROR_STATUS_INFO("ErrorStatusInfo"),
	POWER_STATUS("PowerStatus");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of Decoder monitoring metric
	 */
	DeviceInfoMetric(String name) {
		this.name = name;
	}

	/**
	 * retrieve {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

}

