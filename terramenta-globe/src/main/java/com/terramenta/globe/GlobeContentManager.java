/**
 * Copyright © 2014, Terramenta. All rights reserved. * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or the Common Development and Distribution
 * License("CDDL") (collectively, the "License"). You may not use this work except in compliance
 * with the License.
 *
 * You can obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 *
 */
package com.terramenta.globe;

import com.terramenta.globe.dnd.DragController;
import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.globe.utilities.EciController;
import com.terramenta.globe.utilities.QuickTipController;
import com.terramenta.globe.utilities.SelectController;
import com.terramenta.time.DateProvider;
import gov.nasa.worldwind.StereoSceneController;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.FlatGlobe;
import gov.nasa.worldwind.globes.Globe;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.StarsLayer;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.layers.WorldMapLayer;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwind.view.orbit.FlatOrbitView;
import gov.nasa.worldwindx.examples.ClickAndGoSelectListener;
import gov.nasa.worldwindx.examples.util.HighlightController;
import gov.nasa.worldwindx.examples.util.StatusLayer;
import com.terramenta.globe.solar.Sun;
import com.terramenta.globe.solar.SunDependent;
import java.time.Instant;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris.Heidt
 */
@ServiceProvider(service = com.terramenta.globe.GlobeContentManager.class)
public final class GlobeContentManager implements PreferenceChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(GlobeContentManager.class);
    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final EciController eciController = Lookup.getDefault().lookup(EciController.class);
    private static final Sun sun = Lookup.getDefault().lookup(Sun.class);
    private static final Globe roundGlobe = wwm.getWorldWindow().getModel().getGlobe();
    private static final FlatGlobe flatGlobe = new EarthFlat();
    private static final QuickTipController quickTipController = new QuickTipController(wwm.getWorldWindow());
    private boolean eci = false;// use Earth-centered inertial or Earth-centered, Earth-fixed
    private String flatProjection = FlatGlobe.PROJECTION_MERCATOR;
    private StarsLayer starLayer;

    /**
     *
     */
    public GlobeContentManager() {

        //tweak base layers
        initLayers();

        setStatusLayerType(prefs.get("options.globe.statusBar", "STANDARD"));
        setFlat(prefs.getBoolean("options.globe.isFlat", false));
        setFlatProjection(prefs.get("options.globe.flatProjection", "Lat Lon"));
        setECI(prefs.getBoolean("options.globe.isECI", false));

        // Add controllers to manage selection, highlighting, tool tips, kml, etc.
        quickTipController.setArmed(Boolean.parseBoolean(prefs.get("options.globe.quickTips", "true")));
        new SelectController(wwm.getWorldWindow()).setArmed(true);
        new DragController(wwm.getWorldWindow()).setArmed(true);
        new HighlightController(wwm.getWorldWindow(), SelectEvent.ROLLOVER);
        //new HotSpotController(wwm.getWorldWindow());
        //new KMLApplicationController(wwm.getWorldWindow());
        //new BalloonController(wwm.getWorldWindow());

        prefs.addPreferenceChangeListener(this);

        //establish datetime listener
        dateProvider.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                Date date;
                if (arg instanceof Date) {
                    date = (Date) arg;
                } else {
                    date = dateProvider.getDate();
                }

                updateGlobe(date.toInstant());
                wwm.getWorldWindow().redraw();
            }
        });
        dateProvider.setDate(dateProvider.getDate()); //trigger the above listener
    }

    private void initLayers() {
        LayerList ll = wwm.getLayers();

        //Default Layers
        for (Layer layer : ll) {
            if (layer instanceof CompassLayer) {
                CompassLayer compassLayer = (CompassLayer) layer;
                compassLayer.setIconScale(2 / 10d);
                compassLayer.setLocationOffset(new Vec4(18, 5));
            } else if (layer instanceof WorldMapLayer) {
                wwm.getWorldWindow().addSelectListener(new ClickAndGoSelectListener(wwm.getWorldWindow(), WorldMapLayer.class));
            } else if (layer instanceof ViewControlsLayer) {
                ViewControlsLayer viewControlsLayer = (ViewControlsLayer) layer;
                viewControlsLayer.setLayout(AVKey.VERTICAL);
                viewControlsLayer.setScale(6 / 10d);
                viewControlsLayer.setPosition(AVKey.NORTHEAST);
                viewControlsLayer.setLocationOffset(new Vec4(11, -45));
                //viewControlsLayer.setShowFovControls(true); //these controls confuse people...
                viewControlsLayer.setShowLookControls(true);
                wwm.getWorldWindow().addSelectListener(new ViewControlsSelectListener(wwm.getWorldWindow(), viewControlsLayer));
            } else if (layer instanceof StarsLayer) {
                this.starLayer = (StarsLayer) layer; //Save a reference for rotation
            } else if (layer instanceof SunDependent) {
                ((SunDependent) layer).setSun(sun);
            }
        }
    }

    /**
     *
     * @param type
     */
    private void setStatusLayerType(String type) {
        //Remove old layer
        LayerList ll = wwm.getLayers();
        for (Layer l : ll) {
            if (l instanceof StatusLayer) {
                ll.remove(l);
            }
        }

        if (type == null) {
            type = "STANDARD";
        }

        StatusLayer statusLayer;
        if (type.equalsIgnoreCase("UTM")) {
            statusLayer = new StatusLayer.StatusUTMLayer();
        } else if (type.equalsIgnoreCase("MGRS")) {
            statusLayer = new StatusLayer.StatusMGRSLayer();
        } else {
            statusLayer = new StatusLayer();
        }

        statusLayer.setName("Status Layer");
        statusLayer.setEnabled(true);
        statusLayer.setEventSource(wwm.getWorldWindow());

        //Add new one
        ll.add(statusLayer);
    }

    private double previousRotationalDegree = 0;

    /**
     *
     * @param date
     */
    public void updateGlobe(Instant date) {
        double rotationalDegree = eciController.calculateRotationalDegree(date);

        if (isECI()) {
            // need to do something to keep the ECI view moving even after user interaction
            wwm.getWorldWindow().getView().stopMovement();

            // update rotation of view and Stars
            double theta0 = previousRotationalDegree;
            double thetaf = rotationalDegree;
            double rotateEarthDelta = thetaf - theta0; // amount to rotate the globe around poles axis in degrees

            Position pos = ((BasicOrbitView) wwm.getWorldWindow().getView()).getCenterPosition();
            Position newPos = pos.add(new Position(Angle.fromDegrees(0), Angle.fromDegrees(-rotateEarthDelta), 0.0));
            ((BasicOrbitView) wwm.getWorldWindow().getView()).setCenterPosition(newPos);
        }

        if (starLayer != null) {
            starLayer.setLongitudeOffset(Angle.fromDegrees(-rotationalDegree));
        }

        previousRotationalDegree = rotationalDegree;
    }

    /**
     *
     * @return
     */
    public boolean isECI() {
        return this.eci;
    }

    /**
     *
     * @param state
     */
    public void setECI(boolean state) {
        this.eci = state;
        if (starLayer != null) {
            if (state) {
                starLayer.setLongitudeOffset(Angle.fromDegrees(-eciController.getCurrentRotationalDegree())); // update stars
            } else {
                starLayer.setLongitudeOffset(Angle.fromDegrees(0.0)); // reset to normal
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isFlat() {
        return wwm.getWorldWindow().getModel().getGlobe() instanceof FlatGlobe;
    }

    /**
     *
     * @param flat
     */
    private void setFlat(boolean flat) {
        if (isFlat() == flat) {
            return;
        }

        if (!flat) {
            // Switch to round globe
            wwm.getWorldWindow().getModel().setGlobe(roundGlobe);
            // Switch to orbit view and update with current position
            FlatOrbitView flatOrbitView = (FlatOrbitView) wwm.getWorldWindow().getView();
            BasicOrbitView orbitView = new BasicOrbitView();
            orbitView.setCenterPosition(flatOrbitView.getCenterPosition());
            orbitView.setZoom(flatOrbitView.getZoom());
            orbitView.setHeading(flatOrbitView.getHeading());
            orbitView.setPitch(flatOrbitView.getPitch());
            wwm.getWorldWindow().setView(orbitView);
        } else {
            // Switch to flat globe
            wwm.getWorldWindow().getModel().setGlobe(flatGlobe);
            flatGlobe.setProjection(this.flatProjection);
            // Switch to flat view and update with current position
            BasicOrbitView orbitView = (BasicOrbitView) wwm.getWorldWindow().getView();
            FlatOrbitView flatOrbitView = new FlatOrbitView();
            flatOrbitView.setCenterPosition(orbitView.getCenterPosition());
            flatOrbitView.setZoom(orbitView.getZoom());
            flatOrbitView.setHeading(orbitView.getHeading());
            flatOrbitView.setPitch(orbitView.getPitch());
            wwm.getWorldWindow().setView(flatOrbitView);
        }

        wwm.getWorldWindow().redraw();
    }

    /**
     *
     * @param item
     */
    private void setFlatProjection(String item) {
        if (item.equalsIgnoreCase("Mercator")) {
            this.flatProjection = FlatGlobe.PROJECTION_MERCATOR;
        } else if (item.equalsIgnoreCase("Sinusoidal")) {
            this.flatProjection = FlatGlobe.PROJECTION_SINUSOIDAL;
        } else if (item.equalsIgnoreCase("Modified Sinusoidal")) {
            this.flatProjection = FlatGlobe.PROJECTION_MODIFIED_SINUSOIDAL;
        } else {
            this.flatProjection = FlatGlobe.PROJECTION_LAT_LON;
        }

        if (!isFlat()) {
            return;
        }
        flatGlobe.setProjection(this.flatProjection);
        wwm.getWorldWindow().redraw();
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        if (evt.getKey().equals("options.globe.isFlat")) {
            logger.debug("isFlat changed");
            setFlat(Boolean.parseBoolean(evt.getNewValue()));
        } else if (evt.getKey().equals("options.globe.isECI")) {
            logger.debug("isECI changed");
            setECI(Boolean.parseBoolean(evt.getNewValue()));
        } else if (evt.getKey().equals("options.globe.flatProjection")) {
            logger.debug("flatProjection changed");
            setFlatProjection(evt.getNewValue());
        } else if (evt.getKey().equals("options.globe.displayMode")) {
            logger.debug("displayMode changed");
            StereoSceneController asc = (StereoSceneController) wwm.getWorldWindow().getSceneController();
            asc.setStereoMode(evt.getNewValue());
            wwm.getWorldWindow().redraw();
        } else if (evt.getKey().equals("options.globe.focusAngle")) {
            logger.debug("focusAngle changed");
            StereoSceneController asc = (StereoSceneController) wwm.getWorldWindow().getSceneController();
            asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(evt.getNewValue()) / 10));
            wwm.getWorldWindow().redraw();
        } else if (evt.getKey().equals("options.globe.statusBar")) {
            logger.debug("statusBar changed");
            setStatusLayerType(evt.getNewValue());
        } else if (evt.getKey().equals("options.globe.quickTips")) {
            logger.debug("quickTips changed");
            quickTipController.setArmed(Boolean.parseBoolean(evt.getNewValue()));
        }
    }

}
