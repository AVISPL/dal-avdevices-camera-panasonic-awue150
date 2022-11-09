/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of gain modes
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public enum Gain {

	AUTO("Auto", "OGU:0x"),
	G_0("0", "OGU:0x08"),
	G_1("1", "OGU:0x09"),
	G_2("2", "OGU:0x0A"),
	G_3("3", "OGU:0x0B"),
	G_4("4", "OGU:0x0C"),
	G_5("5", "OGU:0x0D"),
	G_6("6", "OGU:0x0E"),
	G_7("7", "OGU:0x0F"),
	G_8("8", "OGU:0x10"),
	G_9("9", "OGU:0x11"),
	G_10("10", "OGU:0x12"),
	G_11("11", "OGU:0x13"),
	G_12("12", "OGU:0x14"),
	G_13("13", "OGU:0x15"),
	G_14("14", "OGU:0x16"),
	G_15("15", "OGU:0x17"),
	G_16("16", "OGU:0x18"),
	G_17("17", "OGU:0x19"),
	G_18("18", "OGU:0x1A"),
	G_19("19", "OGU:0x1B"),
	G_20("20", "OGU:0x1C"),
	G_21("21", "OGU:0x1D"),
	G_22("22", "OGU:0x1E"),
	G_23("23", "OGU:0x1F"),
	G_24("24", "OGU:0x20"),
	G_25("25", "OGU:0x21"),
	G_26("26", "OGU:0x22"),
	G_27("27", "OGU:0x23"),
	G_28("28", "OGU:0x24"),
	G_29("29", "OGU:0x25"),
	G_30("30", "OGU:0x26"),
	G_31("31", "OGU:0x28"),
	G_32("32", "OGU:0x27"),
	G_33("33", "OGU:0x29"),
	G_34("34", "OGU:0x2A"),
	G_35("35", "OGU:0x2B"),
	G_36("36", "OGU:0x2C"),
	G_37("37", "OGU:0x2D"),
	G_38("38", "OGU:0x2E"),
	G_39("39", "OGU:0x2F"),
	G_40("40", "OGU:0x30"),
	G_41("41", "OGU:0x31"),
	G_42("42", "OGU:0x32"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of gain status
	 * @param apiName api name of gain status
	 */
	Gain(String uiName, String apiName) {
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
	 * This method is used to get shutter mode from api values
	 *
	 * @param apiValues is the set of live camera info value
	 * @return Shutter is the Shutter status that want to get
	 */
	public static Gain getByAPIValue(Map<String, String> apiValues) {
		Optional<Gain> gain = Arrays.stream(Gain.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return gain.orElse(G_0);
	}

	/**
	 * This method is used to get gain status by uiName values
	 *
	 * @param uiName is the uiName of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByUIName(String uiName) {
		Optional<Gain> gain = Arrays.stream(Gain.values()).filter(status -> status.getUiName().equals(uiName)).findFirst();
		return gain.orElse(G_0);
	}

	/**
	 * This method is used to get gain status by uiName values
	 *
	 * @param apiName is the apiName of gain
	 * @return Gain is the gain status that want to get
	 */
	public static Gain getByAPIName(String apiName) {
		Optional<Gain> gain = Arrays.stream(Gain.values()).filter(status -> status.getApiName().equals(apiName)).findFirst();
		return gain.orElse(G_0);
	}
}

