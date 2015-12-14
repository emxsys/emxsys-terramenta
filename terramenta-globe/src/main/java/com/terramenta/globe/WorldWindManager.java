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
import java.io.Serializable;
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
    private static final Logger logger = Logger.getLogger(WorldWindManager.class.getName());
    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final DatetimeProvider datetimeProvider = Lookup.getDefault().lookup(DatetimeProvider.class);
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private static final EciController eciController = Lookup.getDefault().lookup(EciController.class);

    private final InstanceContent content = new InstanceContent();
    private final Lookup internalLookup = new AbstractLookup(content);
    private ExpandableLookup masterLookup;
    private final SessionState sessionState = new SessionState("." + NbBundle.getBranding());
    private final WorldWindow wwd;

    /**
     * WorldWind Configuration Settings
     */
    static {
        //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");// set the TransformFactory to use the Saxon TransformerFactoryImpl method
        System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing

        //load configuration
        String config = prefs.get("options.globe.worldwindConfig", DEFAULT_CONFIG);
        if (config.isEmpty()) {
            config = DEFAULT_CONFIG;
        }
        System.setProperty("gov.nasa.worldwind.app.config.document", config);
    }

    public WorldWindManager() {
        wwd = (WorldWindow) new WorldWindowGLJPanel();
        wwd.setModel((Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME));

        //Scene Controller
        StereoOptionSceneController asc = (StereoOptionSceneController) wwd.getSceneController();
        asc.setStereoMode(prefs.get("options.globe.displayMode", AVKey.STEREO_MODE_NONE));
        asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(prefs.get("options.globe.focusAngle", "0")) / 10));
        asc.setDeepPickEnabled(true);
        asc.getDrawContext().setValue("DISPLAY_DATE", datetimeProvider.getDatetime());

        datetimeProvider.addChangeListener((oldDatetime, newDatetime) -> {
            DrawContext dc = wwd.getSceneController().getDrawContext();

            //set display date
            dc.setValue("DISPLAY_DATETIME", newDatetime);

            //set display interval
            int linger = tac.getLingerDuration();
            if (linger == 0) {
                //0 linger means items do not ever disapear, so dont set a display interval
                dc.removeKey("DISPLAY_DATETIME_INTERVAL");
            } else {
                //get interval based on play direction
                DatetimeInterval interval;
                if (tac.getPreviousStepDirection() < 0) {
                    //interval from playtime to playtime+linger
                    interval = new DatetimeInterval(newDatetime, newDatetime.plusMillis(linger));
                } else {
                    //interval of time from playtime-linger to playtime
                    interval = new DatetimeInterval(newDatetime.minusMillis(linger), newDatetime);
                }
                dc.setValue("DISPLAY_DATETIME_INTERVAL", interval);
            }

            //set eci values
            dc.setValue("ECI", eciController.isEci());
            dc.setValue("ECI_ROTATION", eciController.calculateRotationalDegree(newDatetime));
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
        logger.info("Saving Session...");
        try {
            sessionState.saveSessionState(wwd);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to save session state!", e);
            return;
        }
        logger.info("Session has been saved.");
    }

    public void restoreSessionState() {
        logger.info("Restoring Session...");
        try {
            sessionState.restoreSessionState(wwd);
            wwd.redraw();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to restore session state!", e);
            return;
        }
        logger.info("Session has been restored.");
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
