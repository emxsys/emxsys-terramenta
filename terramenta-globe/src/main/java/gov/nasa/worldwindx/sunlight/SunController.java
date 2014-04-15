/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.worldwindx.sunlight;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.terrain.Tessellator;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author Chris.Heidt
 */
public class SunController implements PropertyChangeListener {

    private static final Logger logger = Logger.getLogger(SunController.class.getName());
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    //private final AtmosphereLayer atmosphereLayer;
    private final RectangularNormalTessellator suntessellator;
    private final SunLayer sunLayer;
    private final Tessellator originalTessellator;
    //private final SkyGradientLayer originalAtmosphere;

    public SunController(SunLayer sunLayer) {
        this.sunLayer = sunLayer;
        this.originalTessellator = wwm.getWorldWindow().getModel().getGlobe().getTessellator();
        this.suntessellator = new RectangularNormalTessellator();
        //this.atmosphereLayer = new AtmosphereLayer();

//        List<Layer> atmos = wwm.getWorldWindow().getModel().getLayers().getLayersByClass(SkyGradientLayer.class);
//        if (!atmos.isEmpty()) {
//            originalAtmosphere = (SkyGradientLayer) atmos.get(0);
//        } else {
//            originalAtmosphere = new SkyGradientLayer();
//            wwm.getWorldWindow().getModel().getLayers().add(originalAtmosphere);
//        }

        sunLayer.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("Enabled")) {
            boolean enabled = (Boolean) evt.getNewValue();
            if (enabled) { // enable shading, use AtmosphereLayer
                wwm.getWorldWindow().getModel().getGlobe().setTessellator(suntessellator);
//                for (int i = 0; i < wwm.getWorldWindow().getModel().getLayers().size(); i++) {
//                    Layer l = wwm.getWorldWindow().getModel().getLayers().get(i);
//                    if (l instanceof SkyGradientLayer) {
//                        this.atmosphereLayer.setEnabled(l.isEnabled());
//                        wwm.getWorldWindow().getModel().getLayers().set(i, this.atmosphereLayer);
//                        break;
//                    }
//                }
            } else { // disable lighting, use SkyGradientLayer
                wwm.getWorldWindow().getModel().getGlobe().setTessellator(originalTessellator);
//                for (int i = 0; i < wwm.getWorldWindow().getModel().getLayers().size(); i++) {
//                    Layer l = wwm.getWorldWindow().getModel().getLayers().get(i);
//                    if (l instanceof AtmosphereLayer) {
//                        this.originalAtmosphere.setEnabled(l.isEnabled());
//                        wwm.getWorldWindow().getModel().getLayers().set(i, this.originalAtmosphere);
//                        break;
//                    }
//                }
            }
        }
    }

    /**
     *
     * @param date
     */
    public void update(Date date) {
        double[] ll = subsolarPoint(date);
        Position sunPosition = new Position(LatLon.fromRadians(ll[0], ll[1]), 0);
        logger.log(Level.FINE, "SUN Position: {0}", sunPosition.toString());
        Vec4 sunVector = wwm.getWorldWindow().getModel().getGlobe().computePointFromPosition(sunPosition).normalize3();

        this.sunLayer.setSunDirection(sunVector);
        this.suntessellator.setLightDirection(sunVector.getNegative3());
        //this.atmosphereLayer.setSunDirection(sunVector);
    }

    /**
     * Calculate the LatLon of sun at given time
     *
     * @param date
     * @return
     */
    public static double[] subsolarPoint(Date date) {
        // Main variables
        double elapsedJulianDays;
        double decimalHours;
        double eclipticLongitude;
        double eclipticObliquity;
        double rightAscension, declination;

        // Calculate difference in days between the current Julian Day
        // and JD 2451545.0, which is noon 1 January 2000 Universal Time
        {
            Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            time.setTime(date);

            // Calculate time of the day in UT decimal hours
            decimalHours = time.get(Calendar.HOUR_OF_DAY)
                    + (time.get(Calendar.MINUTE) + time.get(Calendar.SECOND) / 60.0)
                    / 60.0;
            // Calculate current Julian Day
            long aux1 = (time.get(Calendar.MONTH) - 14) / 12;
            long aux2 = (1461 * (time.get(Calendar.YEAR) + 4800 + aux1)) / 4
                    + (367 * (time.get(Calendar.MONTH) - 2 - 12 * aux1)) / 12
                    - (3 * ((time.get(Calendar.YEAR) + 4900 + aux1) / 100)) / 4
                    + time.get(Calendar.DAY_OF_MONTH) - 32075;
            double julianDate = (double) (aux2) - 0.5 + decimalHours / 24.0;
            // Calculate difference between current Julian Day and JD 2451545.0
            elapsedJulianDays = julianDate - 2451545.0;
        }

        // Calculate ecliptic coordinates (ecliptic longitude and obliquity of the
        // ecliptic in radians but without limiting the angle to be less than 2*Pi
        // (i.e., the result may be greater than 2*Pi)
        {
            double omega = 2.1429 - 0.0010394594 * elapsedJulianDays;
            double meanLongitude = 4.8950630 + 0.017202791698 * elapsedJulianDays; // Radians
            double meanAnomaly = 6.2400600 + 0.0172019699 * elapsedJulianDays;
            eclipticLongitude = meanLongitude + 0.03341607
                    * Math.sin(meanAnomaly) + 0.00034894
                    * Math.sin(2 * meanAnomaly) - 0.0001134 - 0.0000203
                    * Math.sin(omega);
            eclipticObliquity = 0.4090928 - 6.2140e-9 * elapsedJulianDays
                    + 0.0000396 * Math.cos(omega);
        }

        // Calculate celestial coordinates ( right ascension and declination ) in radians
        // but without limiting the angle to be less than 2*Pi (i.e., the result may be
        // greater than 2*Pi)
        {
            double sinEclipticLongitude = Math.sin(eclipticLongitude);
            double dY = Math.cos(eclipticObliquity) * sinEclipticLongitude;
            double dX = Math.cos(eclipticLongitude);
            rightAscension = Math.atan2(dY, dX);
            if (rightAscension < 0.0) {
                rightAscension = rightAscension + Math.PI * 2.0;
            }
            declination = Math.asin(Math.sin(eclipticObliquity) * sinEclipticLongitude);
        }

        double greenwichMeanSiderealTime = 6.6974243242 + 0.0657098283 * elapsedJulianDays + decimalHours;
        double longitude = rightAscension - Math.toRadians(greenwichMeanSiderealTime * 15.0);

        //longitude += Math.PI;//This was putting the sun on the wrong side of the earth!!

        while (declination > Math.PI / 2.0) {
            declination -= Math.PI;
        }
        while (declination <= -Math.PI / 2.0) {
            declination += Math.PI;
        }
        while (longitude > Math.PI) {
            longitude -= Math.PI * 2.0;
        }
        while (longitude <= -Math.PI) {
            longitude += Math.PI * 2.0;
        }

        return new double[]{declination, longitude};
    }
}
