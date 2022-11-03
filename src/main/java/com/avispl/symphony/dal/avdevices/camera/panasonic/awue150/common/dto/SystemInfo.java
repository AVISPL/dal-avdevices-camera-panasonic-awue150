package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Set of system information
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/26/2022
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SystemInfo {

	@JsonAlias("MAC")
	private String macAddress;

	@JsonAlias("SERIAL")
	private String serialNumber;

	@JsonAlias("VERSION")
	private String firmwareVersion;

	/**
	 * Retrieves {@link #macAddress}
	 *
	 * @return value of {@link #macAddress}
	 */
	public String getMacAddress() {
		return macAddress;
	}

	/**
	 * Sets {@link #macAddress} value
	 *
	 * @param macAddress new value of {@link #macAddress}
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	/**
	 * Retrieves {@link #serialNumber}
	 *
	 * @return value of {@link #serialNumber}
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Sets {@link #serialNumber} value
	 *
	 * @param serialNumber new value of {@link #serialNumber}
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Retrieves {@link #firmwareVersion}
	 *
	 * @return value of {@link #firmwareVersion}
	 */
	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * Sets {@link #firmwareVersion} value
	 *
	 * @param firmwareVersion new value of {@link #firmwareVersion}
	 */
	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

}
