/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.interfaces.TemporalObject;
import com.terramenta.time.DateTimeChangeEvent;
import com.terramenta.time.DateTimeController;
import com.terramenta.time.DateTimeEventListener;
import com.terramenta.time.actions.TimeActionController;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PointPlacemark;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.openide.util.Lookup;

/**
 *
 * @author chris.heidt
 */
public class TemporalPlacemark extends PointPlacemark implements TemporalObject {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private DateTime datetime;
    private DateTimeEventListener dateTimeEventListener = new DateTimeEventListener() {

        @Override
        public void changeEventOccurred(DateTimeChangeEvent evt) {
            Duration linger = tac.getLingerDuration();
            if (linger == null) {
                setVisible(true); //null linger means items do not ever disapear
                return;
            }

            Interval interval = new Interval(linger, evt.getDateTime());//interval of time from playtime-linger to playtime
            setVisible(interval.contains(getDateTime()));
        }
    };

    public TemporalPlacemark(Position pstn) {
        super(pstn);
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);//assuming no altitude for now.
        this.setEnableBatchPicking(false); //must be false to support the mutiselection menu(right-click menu) 
        //this.setLineEnabled(true); //Pointless for ground-based points, change when we get items with altitude
    }

    @Override
    public void setDateTime(DateTime datetime) {
        this.datetime = datetime;

        DateTimeController dtc = DateTimeController.getInstance();
        if (datetime != null) {
            dtc.addDateTimeEventListener(dateTimeEventListener);
            dtc.doFire(); //trigger the above listener
        } else {
            dtc.removeDateTimeEventListener(dateTimeEventListener);
        }
    }

    @Override
    public DateTime getDateTime() {
        return this.datetime;
    }
}
