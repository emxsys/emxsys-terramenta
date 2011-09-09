/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.globe;

import com.qna.terramenta.utilities.OrbitUtilities;
import com.qna.terramenta.utilities.SelectController;
import com.qna.terramenta.actions.ObjectGotoAction;
import com.qna.terramenta.globe.options.GlobeOptions;
import com.qna.terramenta.time.DateTimeChangeEvent;
import com.qna.terramenta.time.DateTimeController;
import com.qna.terramenta.time.DateTimeEventListener;
import com.qna.terramenta.utilities.QuickTipController;
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
import gov.nasa.worldwindx.sunlight.SunLayer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import org.joda.time.DateTime;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.actions.SystemAction;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.qna.terramenta.globe//Globe//EN", autostore = false)
@TopComponent.Description(preferredID = "GlobeTopComponent", iconBase = "images/world.png", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "com.qna.terramenta.globe.GlobeTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_GlobeAction", preferredID = "GlobeTopComponent")
public final class GlobeTopComponent extends TopComponent implements PreferenceChangeListener {

    private static final Logger logger = Logger.getLogger(GlobeTopComponent.class.getName());
    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final OrbitUtilities orbit = new OrbitUtilities();
    private static final Globe roundGlobe = wwm.getWorldWindow().getModel().getGlobe();
    private static final FlatGlobe flatGlobe = new EarthFlat();
    private static final QuickTipController quickTipController = new QuickTipController(wwm.getWorldWindow());
    private boolean viewModeECI = false;// use Earth-centered inertial or Earth-centered, Earth-fixed
    private String flatProjection = FlatGlobe.PROJECTION_MERCATOR;
    private SunLayer sunLayer = new SunLayer();
    private StarsLayer starLayer;
    private ViewControlsLayer viewControlsLayer;

    /**
     * 
     */
    public GlobeTopComponent() {
        setName(NbBundle.getMessage(GlobeTopComponent.class, "CTL_GlobeTopComponent"));
        setToolTipText(NbBundle.getMessage(GlobeTopComponent.class, "HINT_GlobeTopComponent"));

        initLayers();

        setFlatGlobe(Boolean.parseBoolean(prefs.get("options.globe.isFlat", "false")));
        setFlatProjection(prefs.get("options.globe.flatProjection", "Lat Lon"));

        // Add controllers to manage selection, highlighting, and tool tips.
        quickTipController.setArmed(Boolean.parseBoolean(prefs.get("options.globe.quickTips", "true")));
        new SelectController(wwm.getWorldWindow());
        new HighlightController(wwm.getWorldWindow(), SelectEvent.ROLLOVER);

        //listeners
        wwm.getWorldWindow().addSelectListener(new ViewControlsSelectListener(wwm.getWorldWindow(), viewControlsLayer));
        wwm.getWorldWindow().addSelectListener(new ClickAndGoSelectListener(wwm.getWorldWindow(), WorldMapLayer.class));

        prefs.addPreferenceChangeListener(this);

        //establish datetime listener
        DateTimeController.getInstance().addDateTimeEventListener(new DateTimeEventListener() {

            @Override
            public void changeEventOccurred(DateTimeChangeEvent evt) {
                updateGlobe(evt.getDateTime());
                wwm.getWorldWindow().redraw();
            }
        });
        DateTimeController.getInstance().doFire(); //trigger the above listener

        //Goto position on of object
        SystemAction.get(ObjectGotoAction.class).addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("GOTO")) {
                    wwm.gotoPosition((Position) evt.getNewValue());
                }
            }
        });

        initComponents();
    }

    private void initLayers() {
        LayerList ll = wwm.getLayers();

        //Default Layers
        for (Layer layer : ll) {
            if (layer instanceof CompassLayer) {
                CompassLayer compassLayer = (CompassLayer) layer;
                compassLayer.setIconScale(2 / 10d);
                compassLayer.setLocationOffset(new Vec4(18, 5));
            } else if (layer instanceof ViewControlsLayer) {
                viewControlsLayer = (ViewControlsLayer) layer;
                viewControlsLayer.setLayout(AVKey.VERTICAL);
                viewControlsLayer.setScale(6 / 10d);
                viewControlsLayer.setPosition(AVKey.NORTHEAST);
                viewControlsLayer.setLocationOffset(new Vec4(11, -45));
                //viewControlsLayer.setShowFovControls(true); //these controls confuse people...
                viewControlsLayer.setShowLookControls(true);
            } else if (layer instanceof StarsLayer) {
                this.starLayer = (StarsLayer) layer; //Save a reference for rotation
            }
        }

        if (starLayer == null) {
            this.starLayer = new StarsLayer();
            ll.add(1, starLayer);
        }

        //Sun
        sunLayer.setEnabled(false);
        ll.add(1, sunLayer);

        //Status Layers
        setStatusLayerType("STANDARD");
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

    /**
     * 
     * @param datetime
     */
    public void updateGlobe(DateTime datetime) {
        if (isECIViewMode()) {
            // need to do something to keep the ECI view moving even after user interaction
            wwm.getWorldWindow().getView().stopMovement();

            // update rotation of view and Stars
            double theta0 = orbit.getRotateECIdeg();
            orbit.setCurrentMJD(DateTimeController.convertToMJD(datetime));
            double thetaf = orbit.getRotateECIdeg();
            double rotateEarthDelta = thetaf - theta0; // amount to rotate the globe around poles axis in degrees

            Position pos = ((BasicOrbitView) wwm.getWorldWindow().getView()).getCenterPosition();
            Position newPos = pos.add(new Position(Angle.fromDegrees(0), Angle.fromDegrees(-rotateEarthDelta), 0.0));
            ((BasicOrbitView) wwm.getWorldWindow().getView()).setCenterPosition(newPos);
        } else {
            orbit.setCurrentMJD(DateTimeController.convertToMJD(datetime));
        }

        starLayer.setLongitudeOffset(Angle.fromDegrees(-orbit.getRotateECIdeg()));
        sunLayer.update(datetime);
    }

    /**
     *
     * @return
     */
    public boolean isECIViewMode() {
        return this.viewModeECI;
    }

    /**
     *
     * @param state
     */
    public void setECIViewMode(boolean state) {
        this.viewModeECI = state;
        if (isECIViewMode()) {
            starLayer.setLongitudeOffset(Angle.fromDegrees(-orbit.getRotateECIdeg())); // update stars
        } else {
            starLayer.setLongitudeOffset(Angle.fromDegrees(0.0)); // reset to normal
        }
    }

    /**
     *
     * @return
     */
    public boolean isFlatGlobe() {
        return wwm.getWorldWindow().getModel().getGlobe() instanceof FlatGlobe;
    }

    /**
     *
     * @param flat
     */
    private void setFlatGlobe(boolean flat) {
        if (isFlatGlobe() == flat) {
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

        if (!isFlatGlobe()) {
            return;
        }
        flatGlobe.setProjection(this.flatProjection);
        wwm.getWorldWindow().redraw();
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        if (evt.getKey().equals("options.globe.isFlat")) {
            logger.fine("isFlat changed");
            setFlatGlobe(Boolean.parseBoolean(evt.getNewValue()));
        } else if (evt.getKey().equals("options.globe.flatProjection")) {
            logger.fine("flatProjection changed");
            setFlatProjection(evt.getNewValue());
        } else if (evt.getKey().equals("options.globe.displayMode")) {
            logger.fine("displayMode changed");
            StereoSceneController asc = (StereoSceneController) wwm.getWorldWindow().getSceneController();
            asc.setStereoMode(evt.getNewValue());
            wwm.getWorldWindow().redraw();
        } else if (evt.getKey().equals("options.globe.focusAngle")) {
            logger.fine("focusAngle changed");
            StereoSceneController asc = (StereoSceneController) wwm.getWorldWindow().getSceneController();
            asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(evt.getNewValue()) / 10));
            wwm.getWorldWindow().redraw();
        } else if (evt.getKey().equals("options.globe.statusBar")) {
            logger.fine("statusBar changed");
            setStatusLayerType(evt.getNewValue());
        } else if (evt.getKey().equals("options.globe.quickTips")) {
            logger.fine("quickTips changed");
            quickTipController.setArmed(Boolean.parseBoolean(evt.getNewValue()));
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel globePanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        globePanel.setBackground(new java.awt.Color(0, 0, 0));
        globePanel.setLayout(new java.awt.BorderLayout());
        globePanel.add(wwm.getWorldWindow(), java.awt.BorderLayout.CENTER);
        add(globePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     * 
     */
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    /**
     * 
     */
    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    /**
     * 
     */
    @Override
    protected void componentActivated() {
        super.componentActivated();
        wwm.getWorldWindow().requestFocusInWindow();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
