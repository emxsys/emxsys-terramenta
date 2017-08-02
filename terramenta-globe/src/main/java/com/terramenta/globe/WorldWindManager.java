/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe;

import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.globe.utilities.EciController;
import com.terramenta.time.DatetimeInterval;
import com.terramenta.time.DatetimeProvider;
import com.terramenta.time.actions.TimeActionController;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.StereoOptionSceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwindx.examples.util.SessionState;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = WorldWindManager.class)
public class WorldWindManager implements Lookup.Provider, Serializable {

    public static final String DEFAULT_CONFIG = "worldwind/config/worldwind.xml";
    private static final Logger LOGGER = Logger.getLogger(WorldWindManager.class.getName());
    private static final Preferences PREFS = NbPreferences.forModule(GlobeOptions.class);
    private static final DatetimeProvider DATETIME_PROVIDER = Lookup.getDefault().lookup(DatetimeProvider.class);
    private static final TimeActionController TAC = Lookup.getDefault().lookup(TimeActionController.class);
    private static final EciController ECI_CONTROLLER = Lookup.getDefault().lookup(EciController.class);

    private final InstanceContent content = new InstanceContent();
    private final Lookup internalLookup = new AbstractLookup(content);
    private final SessionState sessionState = new SessionState("." + NbBundle.getBranding());
    private final WorldWindow wwd;

    //lookup instance is lazy loaded
    private ExpandableLookup masterLookup;

    /**
     * WorldWind Configuration Settings
     */
    static {
        //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");// set the TransformFactory to use the Saxon TransformerFactoryImpl method
        System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing

        //load configuration
        //use user preference first
        String config = PREFS.get("options.globe.worldwindConfig", null);
        if (config == null || config.isEmpty()) {
            //try the etc directory
            Path configPath = Paths.get("./etc/worldwind.xml");
            if (Files.exists(configPath)) {
                config = configPath.toAbsolutePath().normalize().toString();
            } else {
                try {
                    //write the local resource to the etc
                    Files.copy(
                            WorldWindManager.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG),
                            configPath,
                            StandardCopyOption.REPLACE_EXISTING);
                    config = configPath.toAbsolutePath().normalize().toString();
                } catch (IOException ex) {
                    //if we fail to write just use the local resources directly
                    LOGGER.log(Level.WARNING, "Failed to write to etc directory!");
                    config = DEFAULT_CONFIG;
                }
            }
        }

        System.setProperty("gov.nasa.worldwind.app.config.document", config);
        LOGGER.log(Level.INFO, "Using WorldWind configuration: {0}", config);
    }

    public WorldWindManager() {
        wwd = (WorldWindow) new WorldWindowGLJPanel();
        wwd.setModel((Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME));

        //Scene Controller
        StereoOptionSceneController asc = (StereoOptionSceneController) wwd.getSceneController();
        asc.setStereoMode(PREFS.get("options.globe.displayMode", AVKey.STEREO_MODE_NONE));
        asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(PREFS.get("options.globe.focusAngle", "0")) / 10));
        asc.setDeepPickEnabled(true);
        asc.getDrawContext().setValue("DISPLAY_DATETIME", DATETIME_PROVIDER.getDatetime());

        DATETIME_PROVIDER.addChangeListener((oldDatetime, newDatetime) -> {
            DrawContext dc = wwd.getSceneController().getDrawContext();

            //set display date
            dc.setValue("DISPLAY_DATETIME", newDatetime);

            //set display interval
            Duration displayDuration = TAC.getDisplayDuration();
            if (Duration.ZERO.equals(displayDuration)) {
                //zero duration means items do not ever disapear, so dont set a display interval
                dc.removeKey("DISPLAY_DATETIME_INTERVAL");
            } else {
                Duration halfDisplayDuration = displayDuration.dividedBy(2);
                DatetimeInterval interval = new DatetimeInterval(newDatetime.minus(halfDisplayDuration), newDatetime.plus(halfDisplayDuration));
                dc.setValue("DISPLAY_DATETIME_INTERVAL", interval);
            }

            //set eci values
            dc.setValue("ECI", ECI_CONTROLLER.isEci());
            dc.setValue("ECI_ROTATION", ECI_CONTROLLER.calculateRotationalDegree(newDatetime));
        });
    }

    /**
     *
     * @return
     */
    public WorldWindow getWorldWindow() {
        return wwd;
    }

    /**
     * Convenience function
     *
     * @return
     */
    public LayerList getLayers() {
        return wwd.getModel().getLayers();
    }

    /**
     *
     * @param that
     */
    public void gotoPosition(Position that) {
        gotoPosition(that, true);
    }

    public void gotoPosition(Position that, boolean animate) {
        View view = wwd.getView();
        if (animate) {
            view.goTo(that, view.getCenterPoint().distanceTo3(view.getEyePoint()));
        } else {
            view.setEyePosition(that);
        }
    }

    public void saveSessionState() {
        LOGGER.info("Saving Session...");
        try {
            sessionState.saveSessionState(wwd);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save session state!", e);
            return;
        }
        LOGGER.info("Session has been saved.");
    }

    public void restoreSessionState() {
        LOGGER.info("Restoring Session...");
        try {
            sessionState.restoreSessionState(wwd);
            wwd.redraw();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to restore session state!", e);
            return;
        }
        LOGGER.info("Session has been restored.");
    }

    @Override
    public Lookup getLookup() {
        if (masterLookup == null) {
            masterLookup = new ExpandableLookup(internalLookup);
        }
        return masterLookup;
    }

    public Lookup addLookup(Lookup newLookup) {
        ExpandableLookup master = (ExpandableLookup) getLookup();
        master.addLookup(newLookup);
        return master;
    }

    private class ExpandableLookup extends ProxyLookup {

        ExpandableLookup(Lookup... lookups) {
            super(lookups);
        }

        public void addLookup(Lookup lookup) {
            synchronized (ExpandableLookup.this) {
                ArrayList<Lookup> list = new ArrayList<>();
                Collections.addAll(list, getLookups());
                list.add(lookup);
                Lookup[] array = list.toArray(new Lookup[list.size()]);
                setLookups(array);
            }
        }

    }
}
