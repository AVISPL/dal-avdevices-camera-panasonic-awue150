package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.dto;

import java.util.List;

import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.focus.AutoFocus;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.AWBMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.Gain;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.IrisMode;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.NightDayFilter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image.Shutter;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.pantilt.PowerOnPosition;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OperationLock;
import com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.monitoing.OutputFormat;
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
	private String zoomPosition;
	private NightDayFilter nighDayFilter;
	private AWBMode awbMode;
	private Shutter shutter;
	private Gain gain;
	private String irisPosition;
	private OutputFormat outputFormat;
	private IrisMode irisMode;
	private String awbRGain;
	private String awbGGain;
	private String awbBGain;
	private String colorTemperature;
	private int panUIValue;
	private int tiltUIValue;
	private int zoomUIValue;
	private int focusUIValue;
	private float irisUIValue;

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

	/**
	 * Retrieves {@link #zoomPosition}
	 *
	 * @return value of {@link #zoomPosition}
	 */
	public String getZoomPosition() {
		return zoomPosition;
	}

	/**
	 * Sets {@link #zoomPosition} value
	 *
	 * @param zoomPosition new value of {@link #zoomPosition}
	 */
	public void setZoomPosition(String zoomPosition) {
		this.zoomPosition = zoomPosition;
	}

	/**
	 * Retrieves {@link #nighDayFilter}
	 *
	 * @return value of {@link #nighDayFilter}
	 */
	public NightDayFilter getNighDayFilter() {
		return nighDayFilter;
	}

	/**
	 * Sets {@link #nighDayFilter} value
	 *
	 * @param nighDayFilter new value of {@link #nighDayFilter}
	 */
	public void setNighDayFilter(NightDayFilter nighDayFilter) {
		this.nighDayFilter = nighDayFilter;
	}

	/**
	 * Retrieves {@link #awbMode}
	 *
	 * @return value of {@link #awbMode}
	 */
	public AWBMode getAwbMode() {
		return awbMode;
	}

	/**
	 * Sets {@link #awbMode} value
	 *
	 * @param awbMode new value of {@link #awbMode}
	 */
	public void setAwbMode(AWBMode awbMode) {
		this.awbMode = awbMode;
	}

	/**
	 * Retrieves {@link #shutter}
	 *
	 * @return value of {@link #shutter}
	 */
	public Shutter getShutter() {
		return shutter;
	}

	/**
	 * Sets {@link #shutter} value
	 *
	 * @param shutter new value of {@link #shutter}
	 */
	public void setShutter(Shutter shutter) {
		this.shutter = shutter;
	}

	/**
	 * Retrieves {@link #gain}
	 *
	 * @return value of {@link #gain}
	 */
	public Gain getGain() {
		return gain;
	}

	/**
	 * Sets {@link #gain} value
	 *
	 * @param gain new value of {@link #gain}
	 */
	public void setGain(Gain gain) {
		this.gain = gain;
	}

	/**
	 * Retrieves {@link #irisPosition}
	 *
	 * @return value of {@link #irisPosition}
	 */
	public String getIrisPosition() {
		return irisPosition;
	}

	/**
	 * Sets {@link #irisPosition} value
	 *
	 * @param irisPosition new value of {@link #irisPosition}
	 */
	public void setIrisPosition(String irisPosition) {
		this.irisPosition = irisPosition;
	}

	/**
	 * Retrieves {@link #outputFormat}
	 *
	 * @return value of {@link #outputFormat}
	 */
	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	/**
	 * Sets {@link #outputFormat} value
	 *
	 * @param outputFormat new value of {@link #outputFormat}
	 */
	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	/**
	 * Retrieves {@link #irisMode}
	 *
	 * @return value of {@link #irisMode}
	 */
	public IrisMode getIrisMode() {
		return irisMode;
	}

	/**
	 * Sets {@link #irisMode} value
	 *
	 * @param irisMode new value of {@link #irisMode}
	 */
	public void setIrisMode(IrisMode irisMode) {
		this.irisMode = irisMode;
	}

	/**
	 * Retrieves {@link #awbRGain}
	 *
	 * @return value of {@link #awbRGain}
	 */
	public String getAwbRGain() {
		return awbRGain;
	}

	/**
	 * Sets {@link #awbRGain} value
	 *
	 * @param awbRGain new value of {@link #awbRGain}
	 */
	public void setAwbRGain(String awbRGain) {
		this.awbRGain = awbRGain;
	}

	/**
	 * Retrieves {@link #awbGGain}
	 *
	 * @return value of {@link #awbGGain}
	 */
	public String getAwbGGain() {
		return awbGGain;
	}

	/**
	 * Sets {@link #awbGGain} value
	 *
	 * @param awbGGain new value of {@link #awbGGain}
	 */
	public void setAwbGGain(String awbGGain) {
		this.awbGGain = awbGGain;
	}

	/**
	 * Retrieves {@link #awbBGain}
	 *
	 * @return value of {@link #awbBGain}
	 */
	public String getAwbBGain() {
		return awbBGain;
	}

	/**
	 * Sets {@link #awbBGain} value
	 *
	 * @param awbBGain new value of {@link #awbBGain}
	 */
	public void setAwbBGain(String awbBGain) {
		this.awbBGain = awbBGain;
	}

	/**
	 * Retrieves {@link #colorTemperature}
	 *
	 * @return value of {@link #colorTemperature}
	 */
	public String getColorTemperature() {
		return colorTemperature;
	}

	/**
	 * Sets {@link #colorTemperature} value
	 *
	 * @param colorTemperature new value of {@link #colorTemperature}
	 */
	public void setColorTemperature(String colorTemperature) {
		this.colorTemperature = colorTemperature;
	}

	/**
	 * Retrieves {@link #panUIValue}
	 *
	 * @return value of {@link #panUIValue}
	 */
	public int getPanUIValue() {
		return panUIValue;
	}

	/**
	 * Sets {@link #panUIValue} value
	 *
	 * @param panUIValue new value of {@link #panUIValue}
	 */
	public void setPanUIValue(int panUIValue) {
		this.panUIValue = panUIValue;
	}

	/**
	 * Retrieves {@link #tiltUIValue}
	 *
	 * @return value of {@link #tiltUIValue}
	 */
	public int getTiltUIValue() {
		return tiltUIValue;
	}

	/**
	 * Sets {@link #tiltUIValue} value
	 *
	 * @param tiltUIValue new value of {@link #tiltUIValue}
	 */
	public void setTiltUIValue(int tiltUIValue) {
		this.tiltUIValue = tiltUIValue;
	}

	/**
	 * Retrieves {@link #zoomUIValue}
	 *
	 * @return value of {@link #zoomUIValue}
	 */
	public int getZoomUIValue() {
		return zoomUIValue;
	}

	/**
	 * Sets {@link #zoomUIValue} value
	 *
	 * @param zoomUIValue new value of {@link #zoomUIValue}
	 */
	public void setZoomUIValue(int zoomUIValue) {
		this.zoomUIValue = zoomUIValue;
	}

	/**
	 * Retrieves {@link #focusUIValue}
	 *
	 * @return value of {@link #focusUIValue}
	 */
	public int getFocusUIValue() {
		return focusUIValue;
	}

	/**
	 * Sets {@link #focusUIValue} value
	 *
	 * @param focusUIValue new value of {@link #focusUIValue}
	 */
	public void setFocusUIValue(int focusUIValue) {
		this.focusUIValue = focusUIValue;
	}

	/**
	 * Retrieves {@link #irisUIValue}
	 *
	 * @return value of {@link #irisUIValue}
	 */
	public float getIrisUIValue() {
		return irisUIValue;
	}

	/**
	 * Sets {@link #irisUIValue} value
	 *
	 * @param irisUIValue new value of {@link #irisUIValue}
	 */
	public void setIrisUIValue(float irisUIValue) {
		this.irisUIValue = irisUIValue;
	}
}
