/*
 *  Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * Set of constants
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public class DeviceConstant {

	/**
	 * private/ non-parameterized constructor to prevent instance initialization
	 */
	private DeviceConstant() {
	}

	public static final char HASH = '#';
	public static final char NEXT_LINE = '\n';
	public static final String COLON = ":";
	public static final String EQUAL = "=";
	public static final String KELVIN = "K";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String HTTPS = "http://";
	public static final String NONE = "None";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String DAY = "Day";
	public static final String NIGHT = "Night";

	public static final String LOCK = "Lock";
	public static final String UNLOCK = "Unlock";
	public static final String SUCCESSFUL = "Successful";
	public static final String APPLY = "Recall";
	public static final String APPLYING = "Recalling";
	public static final String DELETE = "Delete";
	public static final String DELETING = "Deleting";
	public static final String HOME = "Home";
	public static final String SET = "Set";
	public static final String SETTING = "Setting";
	public static final String PRESET = "Preset";
	public static final String DEFAULT_PRESET = "Select a preset";
	public static final String PUSHING = "Pushing";
	public static final String EXECUTING = "Executing";
	public static final String PT_HOME = "Home";
	public static final String UP = "Up";
	public static final String DOWN = "Down";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	public static final String UP_LEFT = "UpLeft";
	public static final String UP_RIGHT = "UpRight";
	public static final String DOWN_LEFT = "DownLeft";
	public static final String DOWN_RIGHT = "DownRight";
	public static final String BASIC = "Basic ";
	public static final String DIGEST = "Digest ";
	public static final String TWO_NUMBER_FORMAT = "%02d";
	public static final String THREE_NUMBER_FORMAT = "%03d";
	public static final String REGEX_TRAILING_OF_FIELD = "\r\n";
	public static final String MAX_FOCUS_API_VALUE = "FFF";
	public static final String MIN_FOCUS_API_VALUE = "555";
	public static final String MAX_ZOOM_API_VALUE = "FFF";
	public static final String MIN_ZOOM_API_VALUE = "555";
	public static final String IS_VALID_CONFIG_MANAGEMENT = "true";
	public static final String MIN_AWB_GAIN_API_VALUE = "670";
	public static final String MAX_AWB_GAIN_API_VALUE = "990";
	public static final float MIN_AWB_GAIN_UI_VALUE = -400;
	public static final float AWB_GAIN_API_UI_VALUE_CONVERT_FACTOR = 800;
	public static final String MAX_IRIS_API_VALUE = "FFF";
	public static final String MIN_IRIS_API_VALUE = "555";
	public static final String CONNECTION_TIME_OUT = "TIME OUT";
	public static final int MIN_PAN_LEFT_API_VALUE = 01;
	public static final int MAX_PAN_LEFT_API_VALUE = 49;
	public static final String PAN_STOP_API_VALUE = "50";
	public static final int MIN_PAN_RIGHT_API_VALUE = 51;
	public static final int MAX_PAN_RIGHT_API_VALUE = 99;
	public static final int MIN_TILT_DOWN_API_VALUE = 01;
	public static final int MAX_TILT_DOWN_API_VALUE = 49;
	public static final int MIN_TILT_UP_API_VALUE = 51;
	public static final int MAX_TILT_UP_API_VALUE = 99;
	public static final String DEFAULT_PAN_POSITION = "8000";
	public static final String MAX_PAN_POSITION_API_VALUE = "FFFF";
	public static final String MIN_PAN_POSITION_API_VALUE= "0000";
	public static final String MAX_TILT_POSITION_API_VALUE = "FFFF";
	public static final String MIN_TILT_POSITION_API_VALUE= "0000";
	public static final String DEFAULT_TILT_POSITION = "8000";
	public static final float MIN_PAN_POSITION_UI = -175f;
	public static final float MIN_TILT_POSITION_UI = -30f;
	public static final float MAX_FOCUS_UI_VALUE = 95f;
	public static final float MIN_FOCUS_UI_VALUE = 0f;
	public static final float MAX_IRIS_UI_VALUE = 25.5f;
	public static final float MIN_IRIS_UI_VALUE = 2.8f;
	public static final float MAX_FOCUS_SPEED_UI_VALUE = 95f;
	public static final float MIN_FOCUS_SPEED_UI_VALUE = 1f;
	public static final float MAX_PAN_TILT_SPEED_UI_VALUE = 50f;
	public static final float MIN_PAN_TILT_SPEED_UI_VALUE = 1f;
	public static final float MAX_ZOOM_UI_VALUE = 999f;
	public static final float MIN_ZOOM_UI_VALUE = 0f;
	public static final float MAX_ZOOM_SPEED_UI_VALUE = 999f;
	public static final float MIN_ZOOM_SPEED_UI_VALUE = 10f;
	public static final int PAN_START_INDEX_IN_SIMULTANEOUS_RESPONSE = 0;
	public static final int PAN_END_INDEX_IN_SIMULTANEOUS_RESPONSE = 4;
	public static final int TILT_END_INDEX_IN_SIMULTANEOUS_RESPONSE = 8;
	public static final int ZOOM_END_INDEX_IN_SIMULTANEOUS_RESPONSE = 11;
	public static final int FOCUS_END_INDEX_IN_SIMULTANEOUS_RESPONSE = 13;
	public static final int IRIS_END_INDEX_IN_SIMULTANEOUS_RESPONSE = 15;
	public static final int PRESET_INDEX_MAX = 100;
	public static final int PRESET_INDEX_MIN = 1;
	public static final int FIST_FIVE_PRESET_INDEX = 5;
	public static final int AFTER_FIST_FIVE_PRESET_INDEX = 6;
	public static final int DEFAULT_PRESET_INDEX = 0;
	public static final int DEFAULT_VALUE= 0;
	public static final int MAX_FAILED_REQUEST = 4;
	public static final int INDEX_TO_ORDINAL_CONVERT_FACTOR = 1;
	public static final float FOCUS_UI_API_CONVERT_FACTOR = 95;
	public static final float ZOOM_UI_API_CONVERT_FACTOR = 999;
	public static final float PAN_UI_API_CONVERT_FACTOR = 350;
	public static final float TILT_UI_API_CONVERT_FACTOR = 240;
	public static final int REBOOT_GRACE_PERIOD_MS = 0;
}

