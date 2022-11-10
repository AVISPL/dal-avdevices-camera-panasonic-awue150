/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of video output format
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum OutputFormat {

	FORMAT_720_60P("720-59.94p", "OSA:87:0x01"),
	FORMAT_720_50P("720-50p", "OSA:87:0x02"),

	FORMAT_1080_60I("1080-59.94i", "OSA:87:0x0OSA:87:0x4"),
	FORMAT_1080_50I("1080-50i", "OSA:87:0x05"),
	FORMAT_1080_30PSF("1080-29.97psF", "OSA:87:0x07"),
	FORMAT_1080_25PSF("1080-25psF", "OSA:87:0x08"),
	FORMAT_1080_24PSF("1080-23.98psF", "OSA:87:0x0A"),
	FORMAT_1080_60P("1080-59.94p", "OSA:87:0x10"),
	FORMAT_1080_50P("1080-50p", "OSA:87:0x11"),
	FORMAT_1080_30P("1080-29.97p", "OSA:87:0x14"),
	FORMAT_1080_25P("1080-25p", "OSA:87:0x15"),
	FORMAT_1080_23P("1080-23.98p (over59.94i)", "OSA:87:0x16"),
	FORMAT_2160_30P("2160-29.97p", "OSA:87:0x17"),
	FORMAT_2160_25P("2160-25p", "OSA:87:0x18"),
	FORMAT_2160_60P("2160-59.94p", "OSA:87:0x19"),
	FORMAT_2160_50P("2160-50p", "OSA:87:0x1A"),
	FORMAT_2160_23P("2160-23.98p", "OSA:87:0x1B"),
	FORMAT_2160_24P(" 2160-24p", "OSA:87:0x21"),
	FORMAT_1080_24P("1080-24p", "OSA:87:0x22"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of video output format
	 * @param apiName api name of video output format
	 */
	OutputFormat(String uiName, String apiName) {
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
	 * This method is used to get output format by API values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return outputFormat is the output format that want to get
	 */
	public static OutputFormat getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(OutputFormat.values()).filter(format -> apiValues.containsKey(format.getApiName())).findFirst().orElse(OutputFormat.ERROR);
	}
}

