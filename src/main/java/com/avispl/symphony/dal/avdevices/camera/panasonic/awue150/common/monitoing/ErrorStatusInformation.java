/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Set of error status
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 5/4/2022
 * @since 1.0.0
 */
public enum ErrorStatusInformation {

	NO_ERROR("NoError", "rER00"),
	ERROR_01("Error01", "rER01"),
	ERROR_02("Error02", "rER02"),
	ERROR_03("Error03", "rER03"),
	ERROR_04("Error04", "rER04"),
	ERROR_05("Error05", "rER05"),
	ERROR_06("Error06", "rER06"),
	ERROR_07("Error07", "rER07"),
	ERROR_08("Error08", "rER08"),
	ERROR_09("Error09", "rER09"),
	ERROR_10("Error10", "rER0a"),
	ERROR_11("Error11", "rER0b"),
	ERROR_12("Error12", "rER0c"),
	ERROR_13("Error13", "rER0d"),
	ERROR_14("Error14", "rER0e"),
	ERROR_15("Error15", "rER0f"),
	ERROR_16("Error16", "rER10"),
	ERROR_17("Error17", "rER11"),
	ERROR_18("Error18", "rER12"),
	ERROR_19("Error19", "rER13"),
	ERROR_20("Error20", "rER14"),
	ERROR_21("Error21", "rER15"),
	ERROR_22("Error22", "rER16"),
	ERROR_23("Error23", "rER17"),
	ERROR_24("Error24", "rER18"),
	ERROR_25("Error25", "rER19"),
	ERROR_26("Error26", "rER1a"),
	ERROR_27("Error27", "rER1b"),
	ERROR_28("Error28", "rER1c"),
	ERROR_29("Error29", "rER1d"),
	ERROR_30("Error30", "rER1e"),
	ERROR_31("Error31", "rER1f"),
	ERROR_32("Error32", "rER20"),
	ERROR_33("Error33", "rER21"),
	ERROR_34("Error34", "rER22"),
	ERROR_35("Error35", "rER23"),
	ERROR_36("Error36", "rER24"),
	ERROR_37("Error37", "rER25"),
	ERROR_38("Error38", "rER26"),
	ERROR_39("Error39", "rER27"),
	ERROR_40("Error40", "rER28"),
	ERROR_41("Error41", "rER29"),
	ERROR_42("Error42", "rER2a"),
	ERROR_43("Error43", "rER2b"),
	ERROR_44("Error44", "rER2c"),
	ERROR_45("Error45", "rER2d"),
	ERROR_46("Error46", "rER2e"),
	ERROR_47("Error47", "rER2f"),
	ERROR_48("Error48", "rER30"),
	ERROR_49("Error49", "rER31"),
	ERROR_50("Error50", "rER32"),
	ERROR_51("Error51", "rER33"),
	ERROR_52("Error52", "rER34"),
	ERROR_53("Error53", "rER35"),
	ERROR_54("Error54", "rER36"),
	ERROR_55("Error55", "rER37"),
	ERROR_56("Error56", "rER38"),
	ERROR_57("Error57", "rER39"),
	ERROR_58("Error58", "rER3a"),
	ERROR_59("Error59", "rER3b"),
	ERROR_60("Error60", "rER3c"),
	ERROR_61("Error61", "rER3d"),
	ERROR_62("Error62", "rER3e"),
	ERROR_63("Error63", "rER3f"),
	ERROR_64("Error64", "rER40"),
	ERROR_65("Error65", "rER41"),
	ERROR_66("Error66", "rER42"),
	ERROR_67("Error67", "rER43"),
	ERROR_68("Error68", "rER44"),
	ERROR_69("Error69", "rER45"),
	ERROR_70("Error70", "rER46"),
	ERROR_71("Error71", "rER47"),
	ERROR_72("Error72", "rER48"),
	ERROR_73("Error73", "rER49"),
	ERROR_74("Error74", "rER4a"),
	ERROR_75("Error75", "rER4b"),
	ERROR_76("Error76", "rER4c"),
	ERROR_77("Error77", "rER4d"),
	ERROR_78("Error78", "rER4e"),
	ERROR_79("Error79", "rER4f"),
	ERROR_80("Error80", "rER50"),
	ERROR_81("Error81", "rER51"),
	ERROR_82("Error82", "rER52"),
	ERROR_83("Error83", "rER53"),
	ERROR_84("Error84", "rER54"),
	ERROR_85("Error85", "rER55"),
	ERROR_86("Error86", "rER56"),
	ERROR("None", "None");

	private final String uiName;
	private final String apiName;

	/**
	 * Parameterized constructor
	 *
	 * @param uiName ui name of decoder buffering mode
	 * @param apiName  api name of decoder buffering mode
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
		Optional<ErrorStatusInformation> errorStatusInformation = Arrays.stream(ErrorStatusInformation.values()).filter(status -> apiValues.containsKey(status.getApiName())).findFirst();
		return errorStatusInformation.orElse(ErrorStatusInformation.ERROR);
	}
}

