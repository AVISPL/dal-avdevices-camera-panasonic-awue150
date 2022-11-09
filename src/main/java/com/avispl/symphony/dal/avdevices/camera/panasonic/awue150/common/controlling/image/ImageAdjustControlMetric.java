/*
 * Copyright (c) 2022 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.camera.panasonic.awue150.common.controlling.image;

import java.util.Arrays;
import java.util.Optional;

/**
 * Set of focus control metric keys
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/25/2022
 * @since 1.0.0
 */
public enum ImageAdjustControlMetric {

	// Static metric
	IRIS("Iris"),
	IRIS_AUTO_TRIGGER("IrisAutoTrigger"),
	GAIN("Gain"),
	AWB("AWB"),
	AWB_RESET("AWBReset"),
	ABB_RESET("ABBReset"),
	SHUTTER("Shutter"),
	ND_FILTER("NDFilter"),
	AWB_COLOR_TEMPERATURE("AwbColorTemperature "),
	AWB_R_GAIN("AWBRGain"),
	AWB_B_GAIN("AWBBGain"),
	AWB_G_GAIN("AWBGGain"),
	CURRENT_VALUE("CurrentValue"),
	FOCUS_MODE("FocusMode");

	private final String name;

	/**
	 * Parameterized constructor
	 *
	 * @param name Name of controlling metric
	 */
	ImageAdjustControlMetric(String name) {
		this.name = name;
	}

	/**
	 * retrieve {@code {@link #name}}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method is used to get image adjust control metric by name
	 *
	 * @param name is the name of image adjust control that want to get
	 * @return ImageAdjust is the image adjust control that want to get
	 */
	public static ImageAdjustControlMetric getByName(String name) {
		Optional<ImageAdjustControlMetric> imageAdjustControlMetric = Arrays.stream(ImageAdjustControlMetric.values()).filter(control -> control.getName().equals(name)).findFirst();
		if (imageAdjustControlMetric.isPresent()) {
			return imageAdjustControlMetric.get();
		} else {
			throw new IllegalStateException(String.format("Image adjust control %s is not supported.", name));
		}
	}

}

