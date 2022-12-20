/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

import java.util.Arrays;

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
	FAN1_STATUS("Fan1Mode"),
	FAN2_STATUS("Fan2Mode"),
	ERROR_INFORMATION("ErrorInformation"),
	ERROR_STATUS_INFO("ErrorStatusInfo"),
	UHD_CROP("UHDCrop"),
	POWER_STATUS("Power");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of monitoring metric
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


	/**
	 * This method is used to get device info metric by name
	 *
	 * @param name is the name of device info metric that want to get
	 * @return DeviceInfoMetric is the device info metric that want to get
	 */
	public static DeviceInfoMetric getByName(String name) {
		return Arrays.stream(DeviceInfoMetric.values()).filter(metric -> metric.getName().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalStateException(String.format("control group %s is not supported.", name)));
	}
}

