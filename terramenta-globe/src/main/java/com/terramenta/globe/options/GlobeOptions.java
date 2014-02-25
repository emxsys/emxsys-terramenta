/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.options;

import com.terramenta.globe.utilities.CoordinateSystem;
import gov.nasa.worldwind.avlist.AVKey;

/**
 *
 * @author Chris.Heidt
 */
public class GlobeOptions {

    public static final String IS_FLAT = "options.globe.isFlat";
    public static final String FLAT_PROJECTION = "options.globe.flatProjection";
    public static final String DISPLAY_MODE = "options.globe.displayMode";
    public static final String FOCUS_ANGLE = "options.globe.focusAngle";
    public static final String STATUS_BAR = "options.globe.statusBar";
    public static final String WORLDWIND_CONFIG = "options.globe.worldwindConfig";
    public static final String QUICKTIPS = "options.globe.quickTips";
    public static final String CLASSIFICATION = "options.globe.classification";
    public static final String PROTECTION = "options.globe.protection";

    public static final boolean DEFAULT_IS_FLAT = false;
    public static final String DEFAULT_FLAT_PROJECTION = "Lat Lon";
    public static final String DEFAULT_DISPLAY_MODE = AVKey.STEREO_MODE_NONE;
    public static final int DEFAULT_FOCUS_ANGLE = 0;
    public static final String DEFAULT_STATUS_BAR = CoordinateSystem.LatLon.name();
    public static final String DEFAULT_WORLDWIND_CONFIG = "";
    public static final boolean DEFAULT_QUICKTIPS = true;
    public static final String DEFAULT_CLASSIFICATION = "UNCLASSIFIED";
    public static final String DEFAULT_PROTECTION = "";
}
