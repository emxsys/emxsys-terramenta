/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.time.DateTimeChangeEvent;
import com.terramenta.time.DateTimeController;
import com.terramenta.time.DateTimeEventListener;
import com.terramenta.time.actions.TimeActionController;
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
public class TemporalPlacemark extends PointPlacemark {

    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);

    public TemporalPlacemark(Position pstn, final DateTime datetime) {
        super(pstn);

        //establish datetime listener
        DateTimeController.getInstance().addDateTimeEventListener(new DateTimeEventListener() {

            @Override
            public void changeEventOccurred(DateTimeChangeEvent evt) {
                Duration linger = tac.getLingerDuration();
                if (linger == null) {
                    setVisible(true); //null linger means items do not ever disapear
                    return;
                }

                Interval interval = new Interval(linger, evt.getDateTime());//interval of time from playtime-linger to playtime
                setVisible(interval.contains(datetime));
            }
        });
        DateTimeController.getInstance().doFire(); //trigger the above listener
    }
}
