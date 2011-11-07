package gov.nasa.worldwindx.sunlight;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.time.DateTimeController;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SkyGradientLayer;
import gov.nasa.worldwind.terrain.Tessellator;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
public class SunLayer extends LensFlareLayer {

    private static final Logger logger = Logger.getLogger(SunLayer.class.getName());
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private final SkyGradientLayer skyGradientLayer;
    private final AtmosphereLayer atmosphereLayer = new AtmosphereLayer();
    private final RectangularNormalTessellator suntessellator;
    private Tessellator oldtessellator;

    /**
     * 
     */
    public SunLayer() {
        oldtessellator = wwm.getWorldWindow().getModel().getGlobe().getTessellator();
        suntessellator = new RectangularNormalTessellator();

        List<Layer> atmo = wwm.getWorldWindow().getModel().getLayers().getLayersByClass(SkyGradientLayer.class);
        if (!atmo.isEmpty()) {
            skyGradientLayer = (SkyGradientLayer) atmo.get(0);
        } else {
            skyGradientLayer = new SkyGradientLayer();
            wwm.getWorldWindow().getModel().getLayers().add(skyGradientLayer);
        }

        setName("Sun");
        setPickEnabled(false);

        BufferedImage sunDisk = createHaloImage(64, new Color(1f, 1f, .8f), 2f);
        BufferedImage disk = createDiskImage(128, Color.WHITE);
        BufferedImage star = createStarImage(128, Color.WHITE);
        BufferedImage halo = createHaloImage(128, Color.WHITE);
        BufferedImage rainbow = createRainbowImage(128);
        BufferedImage rays = createRaysImage(128, 12, Color.WHITE);

        ArrayList flares = new ArrayList();
        flares.add(new FlareImage(rays, 4, 0, .05));
        flares.add(new FlareImage(star, 1.4, 0, .1));
        flares.add(new FlareImage(star, 2.5, 0, .04));
        flares.add(new FlareImage(sunDisk, .6, 0, .9));
        flares.add(new FlareImage(halo, 1.0, 0, .9));
        flares.add(new FlareImage(halo, 4, 0, .9));
        flares.add(new FlareImage(rainbow, 2.2, 0, .03));
        flares.add(new FlareImage(rainbow, 1.2, 0, .04));
        flares.add(new FlareImage(disk, .1, .4, .1));
        flares.add(new FlareImage(disk, .15, .6, .1));
        flares.add(new FlareImage(disk, .2, .7, .1));
        flares.add(new FlareImage(disk, .5, 1.1, .2));
        flares.add(new FlareImage(disk, .2, 1.3, .1));
        flares.add(new FlareImage(disk, .1, 1.4, .05));
        flares.add(new FlareImage(disk, .1, 1.5, .1));
        flares.add(new FlareImage(disk, .1, 1.6, .1));
        flares.add(new FlareImage(disk, .2, 1.65, .1));
        flares.add(new FlareImage(disk, .12, 1.71, .1));
        flares.add(new FlareImage(disk, 3, 2.2, .05));
        flares.add(new FlareImage(disk, .5, 2.4, .2));
        flares.add(new FlareImage(disk, .7, 2.6, .1));
        flares.add(new FlareImage(rainbow, 5, 3.0, .03));
        flares.add(new FlareImage(disk, .2, 3.5, .1));
        this.addRenderables(flares);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) { // enable shading, use AtmosphereLayer
            oldtessellator = wwm.getWorldWindow().getModel().getGlobe().getTessellator();
            wwm.getWorldWindow().getModel().getGlobe().setTessellator(suntessellator);
            for (int i = 0; i < wwm.getWorldWindow().getModel().getLayers().size(); i++) {
                Layer l = wwm.getWorldWindow().getModel().getLayers().get(i);
                if (l instanceof SkyGradientLayer) {
                    wwm.getWorldWindow().getModel().getLayers().set(i, this.atmosphereLayer);
                }
            }
        } else { // disable lighting, use SkyGradientLayer
            wwm.getWorldWindow().getModel().getGlobe().setTessellator(oldtessellator);
            for (int i = 0; i < wwm.getWorldWindow().getModel().getLayers().size(); i++) {
                Layer l = wwm.getWorldWindow().getModel().getLayers().get(i);
                if (l instanceof AtmosphereLayer) {
                    wwm.getWorldWindow().getModel().getLayers().set(i, this.skyGradientLayer);
                }
            }
        }

        update(DateTimeController.getInstance().getDateTime());
        wwm.getWorldWindow().redraw();
    }

    /**
     *
     * @param datetime
     */
    public void update(DateTime datetime) {
        if (isEnabled()) {
            double[] ll = subsolarPoint(datetime.toGregorianCalendar());
            Position sunPosition = new Position(LatLon.fromRadians(ll[0], ll[1]), 0);
            logger.log(Level.FINE, "SUN Position: {0}", sunPosition.toString());
            Vec4 sunVector = wwm.getWorldWindow().getModel().getGlobe().computePointFromPosition(sunPosition).normalize3();
            this.setSunDirection(sunVector);
            this.suntessellator.setLightDirection(sunVector.getNegative3());
            this.atmosphereLayer.setSunDirection(sunVector);
        }
    }

    /**
     * Calculate the LatLon of sun at given time
     * @param time
     * @return
     */
    public static double[] subsolarPoint(Calendar time) {
        // Main variables
        double elapsedJulianDays;
        double decimalHours;
        double eclipticLongitude;
        double eclipticObliquity;
        double rightAscension, declination;

        // Calculate difference in days between the current Julian Day
        // and JD 2451545.0, which is noon 1 January 2000 Universal Time
        {
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
