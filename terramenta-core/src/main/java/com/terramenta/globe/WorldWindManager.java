/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe;

import com.terramenta.globe.options.GlobeOptions;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.StereoOptionSceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import java.io.Serializable;
import java.util.prefs.Preferences;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = WorldWindManager.class)
public class WorldWindManager implements Serializable {
    
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
    
    public void saveState() {
        System.out.println("WorldWindManager.saveState");
//        File dir = new File(System.getProperty("user.home") + File.separator + ".terramenta");
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
//        File file = new File(dir, this.getClass().getName());
//        try {
//            file.createNewFile();
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//        if (file != null) {
//            ObjectOutputStream out = null;
//            try {
//                out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
//                out.writeObject(getLayers());
//                out.flush();
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            } finally {
//                try {
//                    out.close();
//                } catch (IOException ex) {
//                    //...
//                }
//            }
//        }
    }
    
    public void restoreState() {
        System.out.println("WorldWindManager.restoreState");
//        File file = new File(System.getProperty("user.home") + File.separator + ".terramenta" + File.separator + this.getClass().getName());
//        if (file != null && file.exists()) {
//            ObjectInputStream in = null;
//            try {
//                in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(file)));
//                getLayers().addAll((LayerList) in.readObject());
//            } catch (ClassNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            } finally {
//                try {
//                    in.close();
//                } catch (IOException ex) {
//                    //...
//                }
//            }
//        }
    }
}
