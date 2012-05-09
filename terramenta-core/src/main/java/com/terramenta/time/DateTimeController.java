package com.terramenta.time;

import javax.swing.event.EventListenerList;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.format.ISODateTimeFormat;

/**
 *
 * @author heidtmare
 */
public class DateTimeController implements java.io.Serializable {

    /**
     * 
     */
    public final static int YEAR = 0;
    /**
     *
     */
    public final static int MONTH = 1;
    /**
     *
     */
    public final static int DAY = 2;
    /**
     *
     */
    public final static int HOUR = 3;
    /**
     *
     */
    public final static int MINUTE = 4;
    /**
     *
     */
    public final static int SECOND = 5;
    /**
     *
     */
    public final static int MILLISECOND = 6;
    private DateTime datetime = new DateTime(ISOChronology.getInstanceUTC());
    private EventListenerList listenerList = new EventListenerList();
    private static DateTimeController instance;

    /**
     * 
     * @return
     */
    public static synchronized DateTimeController getInstance() {
        if (instance == null) {
            instance = new DateTimeController();
        }
        return instance;
    }

    /**
     * Constructor
     */
    private DateTimeController() {
        //...
    }

    /**
     * 
     */
    public void doFire() {
        fireTimeEvent(new DateTimeChangeEvent(this, getDateTime()));
    }

    /**
     *
     * @return
     */
    public DateTime getDateTime() {
        return this.datetime;
    }

    /**
     *
     * @param datetime
     */
    public void setDateTime(DateTime datetime) {
        this.datetime = datetime;
        doFire();
    }

    /**
     *
     */
    public void resetDateTime() {
        this.setDateTime(new DateTime(ISOChronology.getInstanceUTC()));
    }

    /**
     *
     * @param unit
     * @param value
     */
    public void add(int unit, int value) {
        if (unit == DateTimeController.MILLISECOND) {
            this.setDateTime(getDateTime().plusMillis(value));
        }
        if (unit == DateTimeController.SECOND) {
            this.setDateTime(getDateTime().plusSeconds(value));
        }
        if (unit == DateTimeController.MINUTE) {
            this.setDateTime(getDateTime().plusMinutes(value));
        }
        if (unit == DateTimeController.HOUR) {
            this.setDateTime(getDateTime().plusHours(value));
        }
        if (unit == DateTimeController.DAY) {
            this.setDateTime(getDateTime().plusDays(value));
        }
        if (unit == DateTimeController.MONTH) {
            this.setDateTime(getDateTime().plusMonths(value));
        }
        if (unit == DateTimeController.YEAR) {
            this.setDateTime(getDateTime().plusYears(value));
        }
    }

    /**
     *
     * @param iso
     * @return
     */
    public static DateTime parseIsoString(String iso) {
        try {
            return ISODateTimeFormat.dateTime().parseDateTime(iso);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param datetime
     * @return
     */
    public static double convertToJD(DateTime datetime) {
        DateTime zero = new DateTime(-4713, 1, 1, 12, 0, 0, 0, JulianChronology.getInstanceUTC());
        Duration d = new Duration(zero, datetime);
        long millis = d.getMillis();
        double seconds = (millis / 1000);
        double days = seconds / 86400;
        return days;
    }

    /**
     *
     * @param datetime
     * @return
     */
    public static double convertToMJD(DateTime datetime) {
        return convertToJD(datetime) - 2400000.5;
    }

    /**
     *
     * @param datetime
     * @return
     */
    public static double convertToMJDE(DateTime datetime) {
        double mjd = convertToMJD(datetime);
        return mjd + deltaT(mjd);
    }

    /**
     *
     * @param listener
     */
    public void addDateTimeEventListener(DateTimeEventListener listener) {
        listenerList.add(DateTimeEventListener.class, listener);
    }

    /**
     *
     * @param listener
     */
    public void removeDateTimeEventListener(DateTimeEventListener listener) {
        listenerList.remove(DateTimeEventListener.class, listener);
    }

    /**
     *
     * @param evt
     */
    private void fireTimeEvent(DateTimeChangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == DateTimeEventListener.class) {
                ((DateTimeEventListener) listeners[i + 1]).changeEventOccurred(evt);
            }
        }
    }

    /**
     * Return TT minus UT.
     *
     * <p>Up to 1983 Ephemeris Time (ET) was used in place of TT, between
     * 1984 and 2000 Temps Dynamique Terrestrial (TDT) was used in place of TT.
     * The three time scales, while defined differently, form a continuous time
     * scale for most purposes.  TT has a fixed offset from TAI (Temps Atomique
     * International).
     *
     * <p>This method returns the difference TT - UT in days.  Usually this
     * would be looked up in a table published after the fact.  Here we use
     * polynomial fits for the distant past, for the future and also for the
     * time where the table exists.  Except for 1987 to 2015, the expressions
     * are taken from
     * Jean Meeus, 1991, <I>Astronomical Algorithms</I>, Willmann-Bell, Richmond VA, p.73f.
     * For the present (1987 to 2015 we use our own graphical linear fit to the
     * data 1987 to 2001 from
     * USNO/RAL, 2001, <I>Astronomical Almanach 2003</I>, U.S. Government Printing Office, Washington DC, Her Majesty's Stationery Office, London, p.K9:
     *
     * <p>t = Ep - 2002
     * <p>DeltaT/s = 9.2 * t / 15 + 65
     *
     * <p>Close to the present (1900 to 1987) we use Schmadl and Zech:
     *
     * <p>t = (Ep - 1900) / 100
     * <p>DeltaT/d = -0.000020      + 0.000297 * t
     *    + 0.025184 * t<sup>2</sup> - 0.181133 * t<sup>3</sup><BR>
     *    + 0.553040 * t<sup>4</sup> - 0.861938 * t<sup>5</sup>
     *    + 0.677066 * t<sup>6</sup> - 0.212591 * t<sup>7</sup>
     *
     * <p>This work dates from 1988 and the equation is supposed to be valid only
     * to 1987, but we extend its use into the near future.  For the 19th
     * century we use Schmadl and Zech:
     *
     * <p>t = (Ep - 1900) / 100
     * <p>DeltaT/d = -0.000009      +  0.003844 * t
     *     +  0.083563 * t<sup>2</sup> +  0.865736 * t<sup>3</sup><BR>
     *     +  4.867575 * t<sup>4</sup> + 15.845535 * t<sup>5</sup>
     *     + 31.332267 * t<sup>6</sup> + 38.291999 * t<sup>7</sup><BR>
     *     + 28.316289 * t<sup>8</sup> + 11.636204 * t<sup>9</sup>
     *     +  2.043794 * t<sup>10</sup>
     *
     * <p>Stephenson and Houlden are credited with the equations for times before
     * 1600.  First for the period 948 to 1600:
     *
     * <p>t = (Ep - 1850) / 100
     * <p>DeltaT/s = 22.5 * t<sup>2</sup>
     *
     * <p>and before 948:
     *
     * <p>t = (Ep - 948) / 100
     * <p>DeltaT/s = 1830 - 405 * t + 46.5 * t<sup>2</sup>
     *
     * <p>This leaves no equation for times between 1600 and 1800 and beyond
     * 2015.  For such times we use the equation of Morrison and Stephenson:
     *
     * <p>t = Ep - 1810
     * <p>DeltaT/s = -15 + 0.00325 * t<sup>2</sup>
     *
     *  @param givenMJD Modified Julian Date (UT)
     *  @return TT minus UT in days
     */
    public static double deltaT(double givenMJD) {
        double theEpoch; /* Julian Epoch */
        double t; /* Time parameter used in the equations. */
        double D; /* The return value. */

        givenMJD -= 50000;

        theEpoch = 2000. + (givenMJD - 1545.) / 365.25;

        /* For 1987 to 2015 we use a graphical linear fit to the annual tabulation
         * from USNO/RAL, 2001, Astronomical Almanach 2003, p.K9.  We use this up
         * to 2015 about as far into the future as it is based on data in the past.
         * The result is slightly higher than the predictions from that source. */
        if (1987 <= theEpoch && 2015 >= theEpoch) {
            t = (theEpoch - 2002.);
            D = 9.2 * t / 15. + 65.;
            D /= 86400.;
        } /* For 1900 to 1987 we use the equation from Schmadl and Zech as quoted in
         * Meeus, 1991, Astronomical Algorithms, p.74.  This is precise within
         * 1.0 second. */ else if (1900 <= theEpoch && 1987 > theEpoch) {
            t = (theEpoch - 1900.) / 100.;
            D = -0.212591 * t * t * t * t * t * t * t
                    + 0.677066 * t * t * t * t * t * t
                    - 0.861938 * t * t * t * t * t
                    + 0.553040 * t * t * t * t
                    - 0.181133 * t * t * t
                    + 0.025184 * t * t
                    + 0.000297 * t
                    - 0.000020;
        } /* For 1800 to 1900 we use the equation from Schmadl and Zech as quoted in
         * Meeus, 1991, Astronomical Algorithms, p.74.  This is precise within 1.0
         * second. */ else if (1800 <= theEpoch && 1900 > theEpoch) {
            t = (theEpoch - 1900.) / 100.;
            D = 2.043794 * t * t * t * t * t * t * t * t * t * t
                    + 11.636204 * t * t * t * t * t * t * t * t * t
                    + 28.316289 * t * t * t * t * t * t * t * t
                    + 38.291999 * t * t * t * t * t * t * t
                    + 31.332267 * t * t * t * t * t * t
                    + 15.845535 * t * t * t * t * t
                    + 4.867575 * t * t * t * t
                    + 0.865736 * t * t * t
                    + 0.083563 * t * t
                    + 0.003844 * t
                    - 0.000009;
        } /* For 948 to 1600 we use the equation from Stephenson and Houlden as
         * quoted in Meeus, 1991, Astronomical Algorithms, p.73. */ else if (948 <= theEpoch && 1600 >= theEpoch) {
            t = (theEpoch - 1850.) / 100.;
            D = 22.5 * t * t;
            D /= 86400.;
        } /* Before 948 we use the equation from Stephenson and Houlden as quoted
         * in Meeus, 1991, Astronomical Algorithms, p.73. */ else if (948 > theEpoch) {
            t = (theEpoch - 948.) / 100.;
            D = 46.5 * t * t - 405. * t + 1830.;
            D /= 86400.;
        } /* Else (between 1600 and 1800 and after 2010) we use the equation from
         * Morrison and Stephenson, quoted as eqation 9.1 in Meeus, 1991,
         * Astronomical Algorithms, p.73. */ else {
            t = theEpoch - 1810.;
            D = 0.00325 * t * t - 15.;
            D /= 86400.;
        }

        return D; // in days
    }

    /**
     * Calculates the Greenwich mean sidereal time (GMST) on julDate (doesn't have to be 0h).
     * Used calculations from Meesus 2nd ed.
     * @param mjd Modified Julian Date
     * @return Greenwich mean sidereal time in degrees (0-360)
     */
    public static double Greenwich_Mean_Sidereal_Deg(double mjd) {
        // calculate T
        double T = (mjd - 51544.5) / 36525.0;

        // do calculation
        double gmst = ((280.46061837 + 360.98564736629 * (mjd - 51544.5)) + 0.000387933 * T * T - T * T * T / 38710000.0) % 360.0;

        // make positive
        if (gmst < 0) {
            gmst += 360.0;
        }

        return gmst;
    }

    /**
     * Calculates the mean sidereal time (MST) on julDate (doesn't have to be 0h) for a given longitiude.
     * @param mjd Modified Julian Date
     * @param longitudeDeg
     * @return mean sidereal time in degrees (0-360)
     */
    public static double Mean_Sidereal_Deg(double mjd, double longitudeDeg) {
        return (Greenwich_Mean_Sidereal_Deg(mjd) + longitudeDeg) % 360.0;
    }
}