/*
 *  Copyright (c) 2021 AVI-SPL, Inc. All Rights Reserved.
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

	private DeviceConstant() {
	}

	public static final char HASH = '#';
	public static final char NEXT_LINE = '\n';
	public static final char SLASH = '/';
	public static final String COMMA = ",";
	public static final String COLON = ":";
	public static final String EQUAL = "=";
	public static final String RIGHT_PARENTHESES = ")";
	public static final String LEFT_PARENTHESES = "(";
	public static final String DASH = "-";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String HTTPS = "http://";
	public static final String NONE = "None";
	public static final String AUTHORIZED = "Authorized";
	public static final String GETTING_DEVICE_INFO_ERR = "Failed to get device info";
	public static final String GETTING_SYSTEM_INFO_ERR = "Failed to get system info";
	public static final String GETTING_DECODER_STATS_ERR = "Failed to get decoder statistic";
	public static final String GETTING_STREAM_STATS_ERR = "Failed to get stream statistic";
	public static final String GETTING_SESSION_ID_ERR = "Login to the device failed,user unauthorized";
	public static final String ROLE_BASED_ERR = "Role based is empty";
	public static final String PORT_NUMBER_ERROR = "Invalid port number";
	public static final String DECODER_CONTROL_ERR = "Failed to control decoder: ";
	public static final String CREATE_STREAM_CONTROL_ERR = "Failed to create stream: ";
	public static final String APPLY_CHANGE_STREAM_CONTROL_ERR = "Failed to control stream: ";
	public static final String DELETE_STREAM_CONTROL_ERR = "Failed to control stream: ";
	public static final String PASSWORD = "password";
	public static final String USERNAME = "username";
	public static final String OPERATOR_ROLE = "Operator";
	public static final String ADMIN_ROLE = "Administrator";
	public static final String SESSION_ID = "Set-Cookie";
	public static final String COOKIE = "Cookie";
	public static final String DAY = " day(s) ";
	public static final String HOUR = " hour(s) ";
	public static final String MINUTE = " minute(s) ";
	public static final String SECOND = " second(s) ";
	public static final String AUDIO_PAIR = "Decoder Audio Pair";
	public static final String ENABLE = "Enable";
	public static final String DISABLE = "Disable";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String APPLY = "Apply";
	public static final String CANCEL = "Cancel";
	public static final String APPLYING = "Applying";
	public static final String CANCELLING = "Canceling";
	public static final String CREATE = "Create";
	public static final String CREATING = "Creating";
	public static final String DELETE = "Delete";
	public static final String DELETING = "Deleting";
	public static final String HOME = "Home";
	public static final String SET = "Set";
	public static final String SETTING = "Setting";
	public static final String PRESET = "Preset";
	public static final String DEFAULT_PRESET = "Select a preset";
	public static final String PUSHING = "Pushing";
	public static final String PT_HOME = "Home";
	public static final String UP = "Up";
	public static final String DOWN = "Down";
	public static final String LEFT = "Left";
	public static final String RIGHT = "Right";
	public static final String UP_LEFT = "UpLeft";
	public static final String UP_RIGHT = "UpRight";
	public static final String DOWN_LEFT_ = "DownLeft";
	public static final String DOWN_RIGHT = "DownRight";
	public static final String BASIC = "Basic ";
	public static final String DIGEST = "Digest ";
	public static final String REGEX_TRAILING_OF_FIELD = "\r\n";
	public static final String MAX_FOCUS_API_VALUE = "FFF";
	public static final String MIN_FOCUS_API_VALUE = "555";
	public static final String MIN_PAN_LEFT_API_VALUE = "01";
	public static final String MAX_PAN_LEFT_API_VALUE = "49";
	public static final String PAN_STOP_API_VALUE = "50";
	public static final String MIN_PAN_RIGHT_API_VALUE = "51";
	public static final String MAX_PAN_RIGHT_API_VALUE = "99";
	public static final String MIN_TILT_LEFT_API_VALUE = "01";
	public static final String MAX_TILT_LEFT_API_VALUE = "49";
	public static final String TILT_STOP_API_VALUE = "50";
	public static final String MIN_TILT_RIGHT_API_VALUE = "51";
	public static final String MAX_TILT_RIGHT_API_VALUE = "99";
	public static final String DEFAULT_PAN_POSITION = "8000";
	public static final String MAX_PAN_POSITION_API_VALUE = "FFFF";
	public static final String MIN_PAN_POSITION_API_VALUE= "0000";
	public static final String MAX_TILT_POSITION_API_VALUE = "FFFF";
	public static final String MIN_TILT_POSITION_API_VALUE= "0000";
	public static final String DEFAULT_TILT_POSITION = "8000";
	public static final float MIN_PAN_POSITION_UI = -175f;
	public static final float MAX_PAN_POSITION_UI = 175f;
	public static final float MIN_TILT_POSITION_UI = -30f;
	public static final float MAX_TILT_POSITION_UI = 210f;
	public static final Float MAX_FOCUS_UI_VALUE = 99f;
	public static final Float MIN_FOCUS_UI_VALUE = 0f;
	public static final Float MIN_FOCUS_SPEED_UI_VALUE = 1f;
	public static final float MAX_PAN_TILT_SPEED_UI_VALUE = 50f;
	public static final float MIN_PAN_TILT_SPEED_UI_VALUE = 1f;
}
