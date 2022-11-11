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

	AUTO("Auto", "OGU:0x", "OGU:00"),
	G_0("0", "OGU:0x08", "OGU:08"),
	G_1("1", "OGU:0x09", "OGU:09"),
	G_2("2", "OGU:0x0A", "OGU:0A"),
	G_3("3", "OGU:0x0B", "OGU:0B"),
	G_4("4", "OGU:0x0C", "OGU:0C"),
	G_5("5", "OGU:0x0D", "OGU:0D"),
	G_6("6", "OGU:0x0E", "OGU:0E"),
	G_7("7", "OGU:0x0F", "OGU:0F"),
	G_8("8", "OGU:0x10", "OGU:10"),
	G_9("9", "OGU:0x11", "OGU:11"),
	G_10("10", "OGU:0x12", "OGU:12"),
	G_11("11", "OGU:0x13", "OGU:13"),
	G_12("12", "OGU:0x14", "OGU:14"),
	G_13("13", "OGU:0x15", "OGU:15"),
	G_14("14", "OGU:0x16", "OGU:16"),
	G_15("15", "OGU:0x17", "OGU:17"),
	G_16("16", "OGU:0x18", "OGU:18"),
	G_17("17", "OGU:0x19", "OGU:19"),
	G_18("18", "OGU:0x1A", "OGU:1A"),
	G_19("19", "OGU:0x1B", "OGU:1B"),
	G_20("20", "OGU:0x1C", "OGU:1C"),
	G_21("21", "OGU:0x1D", "OGU:1D"),
	G_22("22", "OGU:0x1E", "OGU:1E"),
	G_23("23", "OGU:0x1F", "OGU:1F"),
	G_24("24", "OGU:0x20", "OGU:20"),
	G_25("25", "OGU:0x21", "OGU:21"),
	G_26("26", "OGU:0x22", "OGU:22"),
	G_27("27", "OGU:0x23", "OGU:23"),
	G_28("28", "OGU:0x24", "OGU:24"),
	G_29("29", "OGU:0x25", "OGU:25"),
	G_30("30", "OGU:0x26", "OGU:26"),
	G_31("31", "OGU:0x28", "OGU:28"),
	G_32("32", "OGU:0x27", "OGU:27"),
	G_33("33", "OGU:0x29", "OGU:29"),
	G_34("34", "OGU:0x2A", "OGU:2A"),
	G_35("35", "OGU:0x2B", "OGU:2B"),
	G_36("36", "OGU:0x2C", "OGU:2C"),
	G_37("37", "OGU:0x2D", "OGU:2D"),
	G_38("38", "OGU:0x2E", "OGU:2E"),
	G_39("39", "OGU:0x2F", "OGU:2F"),
	G_40("40", "OGU:0x30", "OGU:30"),
	G_41("41", "OGU:0x31", "OGU:31"),
	G_42("42", "OGU:0x32", "OGU:32"),
	ERROR("None", "None", "None");

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
}

