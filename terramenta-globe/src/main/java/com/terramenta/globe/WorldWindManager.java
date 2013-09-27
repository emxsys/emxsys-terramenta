/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe;

import com.terramenta.globe.options.GlobeOptions;
import com.terramenta.time.DateInterval;
import com.terramenta.time.DateProvider;
import com.terramenta.time.actions.TimeActionController;
import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.StereoOptionSceneController;
import gov.nasa.worldwind.View;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.render.DrawContext;
import java.io.Serializable;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author heidtmare
 */
@ServiceProvider(service = WorldWindManager.class)
public class WorldWindManager implements Serializable {

    public static final String DEFAULT_CONFIG = "worldwind/config/worldwind.xml";
    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

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
    private final WorldWindowGLJPanel wwd;
    private Observer dateProviderObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            Date date;
            if (arg instanceof Date) {
                date = (Date) arg;
            } else {
                date = dateProvider.getDate();
            }

            DrawContext dc = wwd.getSceneController().getDrawContext();
            dc.setValue("DISPLAY_DATE", date);

            int linger = tac.getLingerDuration();
            if (linger == 0) {
                //0 linger means items do not ever disapear, so dont set a display interval
                dc.removeKey("DISPLAY_DATEINTERVAL");
                return;
            }

            //get interval based on play direction
            DateInterval interval;
            if (tac.getPreviousStepDirection() < 0) {
                //interval from playtime to playtime+linger
                long startMillis = date.getTime();
                long endMillis = startMillis + linger;
                interval = new DateInterval(startMillis, endMillis);
            } else {
                //interval of time from playtime-linger to playtime
                long endMillis = date.getTime();
                long startMillis = endMillis - linger;
                interval = new DateInterval(startMillis, endMillis);
            }

            dc.setValue("DISPLAY_DATEINTERVAL", interval);
        }
    };

    public WorldWindManager() {
        wwd = new WorldWindowGLJPanel();
        wwd.setModel((Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME));
        
        //Scene Controller
        StereoOptionSceneController asc = (StereoOptionSceneController) wwd.getSceneController();
        asc.setStereoMode(prefs.get("options.globe.displayMode", AVKey.STEREO_MODE_NONE));
        asc.setFocusAngle(Angle.fromDegrees(Double.parseDouble(prefs.get("options.globe.focusAngle", "0")) / 10));
        asc.setDeepPickEnabled(true);
        asc.getDrawContext().setValue("DISPLAY_DATE", dateProvider.getDate());

        dateProvider.addObserver(dateProviderObserver);
    }

    /**
     *
     * @return
     */
    public WorldWindowGLJPanel getWorldWindow() {
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
