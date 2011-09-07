/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.globe;

import com.qna.terramenta.globe.options.GlobeOptions;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.StereoOptionSceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = WorldWindManager.class)
public class WorldWindManager {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);

    /**
     * WorldWind Configuration Settings
     */
    static {
        //System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");// set the TransformFactory to use the Saxon TransformerFactoryImpl method
        System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        System.setProperty("gov.nasa.worldwind.app.config.document", prefs.get("options.globe.worldwindConfig", "worldwind/worldwind.xml"));
    }
    private static final WorldWindowGLJPanel wwd = new WorldWindowGLJPanel();

    public WorldWindManager() {
        //Scene Controller
        StereoOptionSceneController asc = (StereoOptionSceneController) wwd.getSceneController();
        asc.setStereoMode(prefs.get("options.globe.displayMode", AVKey.STEREO_MODE_NONE));
        asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(prefs.get("options.globe.focusAngle", "0")) / 10));
        asc.setDeepPickEnabled(true);

        //Model
        Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        model.setShowWireframeExterior(false);
        model.setShowWireframeInterior(false);
        model.setShowTessellationBoundingVolumes(false);
        wwd.setModel(model);
    }

    /**
     * 
     * @return
     */
    public WorldWindowGLJPanel getWorldWindow() {
        return wwd;
    }

    /**
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
        View view = wwd.getView();
        view.goTo(that, view.getCenterPoint().distanceTo3(view.getEyePoint()));
    }
}
