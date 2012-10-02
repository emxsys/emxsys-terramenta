/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.interfaces.TemporalObject;
import com.terramenta.time.DateProvider;
import com.terramenta.time.actions.TimeActionController;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PointPlacemark;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;

/**
 *
 * @author chris.heidt
 */
public class TemporalPlacemark extends PointPlacemark implements TemporalObject {

    private static final DateProvider dateProvider = Lookup.getDefault().lookup(DateProvider.class);
    private static final TimeActionController tac = Lookup.getDefault().lookup(TimeActionController.class);
    private Observer dateProviderObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            Date date;
            if (arg instanceof Date) {
                date = (Date) arg;
            } else {
                date = dateProvider.getDate();
            }


            int linger = tac.getLingerDuration();
            if (linger == 0) {
                setVisible(true); //0 linger means items do not ever disapear
                return;
            }

            //get interval based on play direction
            long startMillis;
            long endMillis;
            if (tac.getPreviousStepDirection() < 0) {
                //interval from playtime to playtime+linger
                startMillis = date.getTime();
                endMillis = startMillis + linger;
            } else {
                //interval of time from playtime-linger to playtime
                endMillis = date.getTime();
                startMillis = endMillis - linger;
            }

            //does this placmark exist within the interval?
            long eventMillis = getDate().getTime();
            boolean isWithin = (eventMillis > startMillis && eventMillis < endMillis) ? true : false;
            setVisible(isWithin);
        }
    };

    public TemporalPlacemark(Position pstn) {
        super(pstn);
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);//assuming no altitude for now.
        this.setEnableBatchPicking(false); //must be false to support the mutiselection menu(right-click menu) 
        //this.setLineEnabled(true); //Pointless for ground-based points, change when we get items with altitude
    }

    @Override
    public void setDate(Date date) {
        this.setValue("date", date);//AVList

        if (date != null) {
            dateProvider.addObserver(dateProviderObserver);
            dateProvider.setDate(dateProvider.getDate()); //trigger the above listener
        } else {
            dateProvider.deleteObserver(dateProviderObserver);
        }
    }

    @Override
    public Date getDate() {
        return (Date) this.getValue("date");//AVList
    }
}
