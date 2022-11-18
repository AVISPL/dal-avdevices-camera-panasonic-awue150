/*
 *  Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * Controlling and monitoring command
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/28/2022
 * @since 1.0.0
 */
public class Command {

	/**
	 * private/ non-parameterized constructor to prevent instance initialization
	 */
	private Command() {
	}

	// Focus commands
	public static final String FOCUS = "AXF";
	public static final String FOCUS_POSITION_RESPONSE = "axf";

	// Preset commands
	public static final String SET_PRESET = "M";
	public static final String APPLY_PRESET = "R";
	public static final String DELETE_PRESET = "C";
	public static final String HOME_PRESET = "APC80008000";
	public static final String PRESET_RESPONSE = "s";
	public static final String PRESET_00_RESPONSE = "pE00";
	public static final String PRESET_01_RESPONSE = "pE01";
	public static final String PRESET_02_RESPONSE = "pE02";
	public static final char SET_PRESET_SUCCESSFUL_RESPONSE = '1';
	public static final char DELETE_PRESET_SUCCESSFUL_RESPONSE = '0';

	// Pan/ Tilt commands
	public static final String PAN_TILT_UP_LIMITATION = "lC1";
	public static final String PAN_TILT_DOWN_LIMITATION = "lC2";
	public static final String PAN_TILT_LEFT_LIMITATION = "lC3";
	public static final String PAN_TILT_RIGHT_LIMITATION = "lC4";
	public static final String PAN_TILT_STOP = "%23PTS5050";
	public static final String PAN_TILT = "%23PTS";
	public static final String TITLE_RESPONSE = "TITLE:";
	public static final String PAN_TILT_POSITION_RESPONSE = "aPC";
	public static final String ENABLE= "0";
	public static final String DISABLE = "1";

	// Zoom commands
	public static final String ZOOM = "AXZ";
	public static final String ZOOM_POSITION_RESPONSE = "axz";

	// Image adjust
	public static final String IRIS = "AXI";
	public static final String IRIS_POSITION_RESPONSE = "axi";
	public static final String AWB_RESET = "OWS";
	public static final String ABB_RESET = "OAS";

	// Other commands
	public static final String SIMULTANEOUS = "PTD";
	public static final String OPERATION_LOCK_RESPONSE = "OSJ:40:";
	public static final String OPERATION_LOCK_FULL_RESPONSE = "OSJ:40:0";
	public static final String ERROR_RESPONSE_1 = "ER1";
	public static final String ERROR_RESPONSE_2 = "ER2";
	public static final String ERROR_RESPONSE_3 = "ER3";
	public static final String HASH = "%23";
	public static final String COLOR_TEMPERATURE = "OSJ:4A:0x";
	public static final String AWB_R_GAIN = "OSJ:4B:0x";
	public static final String AWB_G_GAIN = "OSJ:4D:0x";
	public static final String AWB_B_GAIN = "OSJ:4C:0x";
	public static final String QUERY_PRESET_NAME = "QSJ:35:";
}
