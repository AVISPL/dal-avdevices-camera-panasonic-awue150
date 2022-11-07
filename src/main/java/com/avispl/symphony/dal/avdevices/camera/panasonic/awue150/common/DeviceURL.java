/*
 *  Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common;

/**
 * URLs which will be accessed
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public class DeviceURL {

	private DeviceURL() {
	}

	public static final String FIRST_LOGIN = "/cgi-bin/first_login";
	public static final String SYSTEM_INFO = "/cgi-bin/getinfo?FILE=1";
	public static final String LIVE_CAMERA_DATA = "/live/camdata.html";
	public static final String MODEL_SERIAL = "/cgi-bin/model_serial";
	public static final String CAMERA_PTZ_CONTROL = "/cgi-bin/aw_ptz?cmd=";
	public static final String CAMERA_CONTROL = "/cgi-bin/aw_cam?cmd=";
	public static final String CAMERA_CONTROL_HASH = "%23";
	public static final String CAMERA_CONTROL_RES = "&res=1";
}
