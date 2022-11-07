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
	 *  private/ non-parameterized constructor to prevent instance initialization
	 */
	private Command() {
	}

	// Focus commands
	public static final String FOCUS = "AXF";
	public static final String AUTO_FOCUS = "OAF:";
	public static final String FOCUS_POSITION_RESPONSE = "axf";

	// Preset commands
	public static final String SET_PRESET = "M";
	public static final String APPLY_PRESET = "M";
	public static final String DELETE_PRESET = "C";
	public static final String HOME_PRESET = "APC80008000";
	public static final String PRESET_RESPONSE = "s";
	public static final String PRESET_00_RESPONSE = "pE00";
	public static final String PRESET_01_RESPONSE = "pE01";
	public static final String PRESET_02_RESPONSE = "pE02";
	public static final char SET_PRESET_SUCCESSFUL_RESPONSE = '1';
	public static final char DELETE_PRESET_SUCCESSFUL_RESPONSE = '0';

	// Pan/ Tilt commands
	public static final String PAN_TILT_RIGHT_DEFAULT = "%23PTS7550";
	public static final String PAN_TILT_LEFT_DEFAULT = "%23PTS2550";
	public static final String PAN_TILT_UP_DEFAULT = "%23PTS5075";
	public static final String PAN_TILT_DOWN_DEFAULT = "%23PTS5025";
	public static final String PAN_TILT_UP_RIGHT_DEFAULT = "%23PTS7575";
	public static final String PAN_TILT_DOWN_RIGHT_DEFAULT = "%23PTS7525";
	public static final String PAN_TILT_UP_LEFT_DEFAULT = "%23PTS2575";
	public static final String PAN_TILT_DOWN_LEFT_DEFAULT = "%23PTS2525";
	public static final String PAN_TILT_STOP = "%23PTS5050";
	public static final String PAN_TILT = "%23PTS";
	public static final String TITLE_RESPONSE = "TITLE:";
	public static final String PAN_TILT_POSITION_RESPONSE = "aPC";

	// Zoom commands
	public static final String ZOOM = "AXZ";
	public static final String ZOOM_POSITION_RESPONSE = "axz";


	// Other commands
	public static final String SIMULTANEOUS = "PTD";
	public static final String OPERATION_LOCK_RESPONSE = "OSJ:40:";
	public static final String OPERATION_LOCK_FULL_RESPONSE = "OSJ:40:0";
	public static final String ERROR_RESPONSE = "ER";
	public static final String HASH = "%23";
}
