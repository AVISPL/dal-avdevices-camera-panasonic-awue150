package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto;


import java.util.List;

import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.AutoFocus;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PowerOnPosition;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OperationLock;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.PowerStatus;

/**
 * Set of live camera information
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/27/2022
 * @since 1.0.0
 */
public class LiveCameraInfo {

	private String focusPosition;
	private AutoFocus autoFocus;
	private String focusADJWithPTZ;
	private String lastPreset;
	private OperationLock operationLock;
	private PowerStatus powerStatus;
	private String panTiltPosition;
	private PowerOnPosition powerOnPosition;
	private String preset01To40;
	private String preset41To80;
	private String preset81To100;
	private List<Character> presetEntitiesStatus;

	/**
	 * Retrieves {@link #focusPosition}
	 *
	 * @return value of {@link #focusPosition}
	 */
	public String getFocusPosition() {
		return focusPosition;
	}

	/**
	 * Sets {@link #focusPosition} value
	 *
	 * @param focusPosition new value of {@link #focusPosition}
	 */
	public void setFocusPosition(String focusPosition) {
		this.focusPosition = focusPosition;
	}

	/**
	 * Retrieves {@link #autoFocus}
	 *
	 * @return value of {@link #autoFocus}
	 */
	public AutoFocus getAutoFocus() {
		return autoFocus;
	}

	/**
	 * Sets {@link #autoFocus} value
	 *
	 * @param autoFocus new value of {@link #autoFocus}
	 */
	public void setAutoFocus(AutoFocus autoFocus) {
		this.autoFocus = autoFocus;
	}

	/**
	 * Retrieves {@link #focusADJWithPTZ}
	 *
	 * @return value of {@link #focusADJWithPTZ}
	 */
	public String getFocusADJWithPTZ() {
		return focusADJWithPTZ;
	}

	/**
	 * Sets {@link #focusADJWithPTZ} value
	 *
	 * @param focusADJWithPTZ new value of {@link #focusADJWithPTZ}
	 */
	public void setFocusADJWithPTZ(String focusADJWithPTZ) {
		this.focusADJWithPTZ = focusADJWithPTZ;
	}

	/**
	 * Retrieves {@link #lastPreset}
	 *
	 * @return value of {@link #lastPreset}
	 */
	public String getLastPreset() {
		return lastPreset;
	}

	/**
	 * Sets {@link #lastPreset} value
	 *
	 * @param lastPreset new value of {@link #lastPreset}
	 */
	public void setLastPreset(String lastPreset) {
		this.lastPreset = lastPreset;
	}

	/**
	 * Retrieves {@link #operationLock}
	 *
	 * @return value of {@link #operationLock}
	 */
	public OperationLock getOperationLock() {
		return operationLock;
	}

	/**
	 * Sets {@link #operationLock} value
	 *
	 * @param operationLock new value of {@link #operationLock}
	 */
	public void setOperationLock(OperationLock operationLock) {
		this.operationLock = operationLock;
	}

	/**
	 * Retrieves {@link #powerStatus}
	 *
	 * @return value of {@link #powerStatus}
	 */
	public PowerStatus getPowerStatus() {
		return powerStatus;
	}

	/**
	 * Sets {@link #powerStatus} value
	 *
	 * @param powerStatus new value of {@link #powerStatus}
	 */
	public void setPowerStatus(PowerStatus powerStatus) {
		this.powerStatus = powerStatus;
	}

	/**
	 * Retrieves {@link #panTiltPosition}
	 *
	 * @return value of {@link #panTiltPosition}
	 */
	public String getPanTiltPosition() {
		return panTiltPosition;
	}

	/**
	 * Sets {@link #panTiltPosition} value
	 *
	 * @param panTiltPosition new value of {@link #panTiltPosition}
	 */
	public void setPanTiltPosition(String panTiltPosition) {
		this.panTiltPosition = panTiltPosition;
	}

	/**
	 * Retrieves {@link #powerOnPosition}
	 *
	 * @return value of {@link #powerOnPosition}
	 */
	public PowerOnPosition getPowerOnPosition() {
		return powerOnPosition;
	}

	/**
	 * Sets {@link #powerOnPosition} value
	 *
	 * @param powerOnPosition new value of {@link #powerOnPosition}
	 */
	public void setPowerOnPosition(PowerOnPosition powerOnPosition) {
		this.powerOnPosition = powerOnPosition;
	}

	/**
	 * Retrieves {@link #preset01To40}
	 *
	 * @return value of {@link #preset01To40}
	 */
	public String getPreset01To40() {
		return preset01To40;
	}

	/**
	 * Sets {@link #preset01To40} value
	 *
	 * @param preset01To40 new value of {@link #preset01To40}
	 */
	public void setPreset01To40(String preset01To40) {
		this.preset01To40 = preset01To40;
	}

	/**
	 * Retrieves {@link #preset41To80}
	 *
	 * @return value of {@link #preset41To80}
	 */
	public String getPreset41To80() {
		return preset41To80;
	}

	/**
	 * Sets {@link #preset41To80} value
	 *
	 * @param preset41To80 new value of {@link #preset41To80}
	 */
	public void setPreset41To80(String preset41To80) {
		this.preset41To80 = preset41To80;
	}

	/**
	 * Retrieves {@link #preset81To100}
	 *
	 * @return value of {@link #preset81To100}
	 */
	public String getPreset81To100() {
		return preset81To100;
	}

	/**
	 * Sets {@link #preset81To100} value
	 *
	 * @param preset81To100 new value of {@link #preset81To100}
	 */
	public void setPreset81To100(String preset81To100) {
		this.preset81To100 = preset81To100;
	}

	/**
	 * Retrieves {@link #presetEntitiesStatus}
	 *
	 * @return value of {@link #presetEntitiesStatus}
	 */
	public List<Character> getPresetEntitiesStatus() {
		return presetEntitiesStatus;
	}

	/**
	 * Sets {@link #presetEntitiesStatus} value
	 *
	 * @param presetEntitiesStatus new value of {@link #presetEntitiesStatus}
	 */
	public void setPresetEntitiesStatus(List<Character> presetEntitiesStatus) {
		this.presetEntitiesStatus = presetEntitiesStatus;
	}
}
