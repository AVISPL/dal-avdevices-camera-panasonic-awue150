/*
 *  Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * URLs which will be accessed
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 3/8/2022
 * @since 1.0.0
 */
public class DeviceURL {

	private DeviceURL() {
	}

	public static final String FIRST_LOGIN = "/cgi-bin/first_login";
	public static final String SYSTEM_INFO = "/cgi-bin/getinfo?FILE=1";
	public static final String LIVE_CAMERA_DATA = "/live/camdata.html";
}
