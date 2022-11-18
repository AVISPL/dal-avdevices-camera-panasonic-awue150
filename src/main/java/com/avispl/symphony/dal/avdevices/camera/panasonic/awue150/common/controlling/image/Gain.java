/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;

/**
 * Set of gain modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum Gain {

	AUTO("Auto", "OGU:0x80", "OGU:80"),
	G_NEGATIVE_3("-3dB", "OGU:0x05", "OGU:05"),
	G_NEGATIVE_2("-2dB", "OGU:0x06", "OGU:06"),
	G_NEGATIVE_1("-1dB", "OGU:0x07", "OGU:07"),
	G_0("0dB", "OGU:0x08", "OGU:08"),
	G_1("1dB", "OGU:0x09", "OGU:09"),
	G_2("2dB", "OGU:0x0A", "OGU:0A"),
	G_3("3dB", "OGU:0x0B", "OGU:0B"),
	G_4("4dB", "OGU:0x0C", "OGU:0C"),
	G_5("5dB", "OGU:0x0D", "OGU:0D"),
	G_6("6dB", "OGU:0x0E", "OGU:0E"),
	G_7("7dB", "OGU:0x0F", "OGU:0F"),
	G_8("8dB", "OGU:0x10", "OGU:10"),
	G_9("9dB", "OGU:0x11", "OGU:11"),
	G_10("10dB", "OGU:0x12", "OGU:12"),
	G_11("11dB", "OGU:0x13", "OGU:13"),
	G_12("12dB", "OGU:0x14", "OGU:14"),
	G_13("13dB", "OGU:0x15", "OGU:15"),
	G_14("14dB", "OGU:0x16", "OGU:16"),
	G_15("15dB", "OGU:0x17", "OGU:17"),
	G_16("16dB", "OGU:0x18", "OGU:18"),
	G_17("17dB", "OGU:0x19", "OGU:19"),
	G_18("18dB", "OGU:0x1A", "OGU:1A"),
	G_19("19dB", "OGU:0x1B", "OGU:1B"),
	G_20("20dB", "OGU:0x1C", "OGU:1C"),
	G_21("21dB", "OGU:0x1D", "OGU:1D"),
	G_22("22dB", "OGU:0x1E", "OGU:1E"),
	G_23("23dB", "OGU:0x1F", "OGU:1F"),
	G_24("24dB", "OGU:0x20", "OGU:20"),
	G_25("25dB", "OGU:0x21", "OGU:21"),
	G_26("26dB", "OGU:0x22", "OGU:22"),
	G_27("27dB", "OGU:0x23", "OGU:23"),
	G_28("28dB", "OGU:0x24", "OGU:24"),
	G_29("29dB", "OGU:0x25", "OGU:25"),
	G_30("30dB", "OGU:0x26", "OGU:26"),
	G_31("31dB", "OGU:0x28", "OGU:28"),
	G_32("32dB", "OGU:0x27", "OGU:27"),
	G_33("33dB", "OGU:0x29", "OGU:29"),
	G_34("34dB", "OGU:0x2A", "OGU:2A"),
	G_35("35dB", "OGU:0x2B", "OGU:2B"),
	G_36("36dB", "OGU:0x2C", "OGU:2C"),
	G_37("37dB", "OGU:0x2D", "OGU:2D"),
	G_38("38dB", "OGU:0x2E", "OGU:2E"),
	G_39("39dB", "OGU:0x2F", "OGU:2F"),
	G_40("40dB", "OGU:0x30", "OGU:30"),
	G_41("41dB", "OGU:0x31", "OGU:31"),
	G_42("42dB", "OGU:0x32", "OGU:32");

	private final String uiName;
	private final String apiNameFirst;

	private final String apiNameSecond;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of gain status
	 * @param apiNameFirst api name of gain status
	 * @param apiNameSecond api name second of gain status
	 */
	Gain(String uiName, String apiNameFirst, String apiNameSecond) {
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
	public static Gain getByAPIValue(Map<String, String> apiValues) {
		return Arrays.stream(Gain.values()).filter(status -> apiValues.containsKey(status.getApiNameFirst())).findFirst().orElse(G_0);
	}

	/**
	 * This method is used to get gain status by uiName value
	 *
	 * @param uiName is the uiName of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByUIName(String uiName) {
		return Arrays.stream(Gain.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(G_0);
	}

	/**
	 * This method is used to get gain status by apiName value
	 *
	 * @param apiNameFirst is the apiNameFirst of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByAPINameFirst(String apiNameFirst) {
		return Arrays.stream(Gain.values()).filter(status -> status.getApiNameFirst().equals(apiNameFirst)).findFirst().orElse(G_0);
	}

	/**
	 * This method is used to get gain status by apiName value
	 *
	 * @param apiName is the apiNameSecond of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByAPINameSecond(String apiName) {
		return Arrays.stream(Gain.values()).filter(status -> status.getApiNameSecond().equals(apiName)).findFirst().orElse(G_0);
	}

	/**
	 * This method is used to get gain status by ui name value
	 *
	 * @param uiName is the ui name of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByUINameSecond(String uiName) {
		return Arrays.stream(Gain.values()).filter(status -> status.getUiName().equals(uiName)).findFirst().orElse(G_0);
	}

}

