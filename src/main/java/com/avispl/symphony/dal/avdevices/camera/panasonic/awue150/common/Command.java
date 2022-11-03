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

	private Command() {
	}

	public static final String FOCUS = "AXF";
	public static final String AUTO_FOCUS = "OAF:";
	public static final String SET_PRESET = "M";
	public static final String APPLY_PRESET = "M";
	public static final String DELETE_PRESET = "C";
	public static final String HOME_PRESET = "APC80008000";
	public static final String PRESET_RESPONSE = "s";
	public static final String ERROR = "ER";
}
