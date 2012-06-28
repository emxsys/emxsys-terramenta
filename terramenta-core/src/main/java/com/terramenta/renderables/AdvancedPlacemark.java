/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.interfaces.ContextualObject;
import com.terramenta.interfaces.SelectableObject;
import com.terramenta.interfaces.TemporalObject;
import com.terramenta.time.DateTimeChangeEvent;
import com.terramenta.time.DateTimeController;
import com.terramenta.time.DateTimeEventListener;
import com.terramenta.time.actions.TimeActionController;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.openide.util.Lookup;

/**
 *
 * @author chris.heidt
 */
public class AdvancedPlacemark extends PointPlacemark implements ContextualObject, TemporalObject, SelectableObject {

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
    /**
     * reference implementation for handing selection events
     */
    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("SELECT") && evt.getNewValue().equals(Boolean.TRUE)) {
                onSelect();
            } else if (evt.getPropertyName().equals("HOVER") && evt.getNewValue().equals(Boolean.TRUE)) {
                onHover();
            } else if (evt.getPropertyName().equals("ROLLOVER") && evt.getNewValue().equals(Boolean.TRUE)) {
                onRollover();
            }
        }
    };

    public AdvancedPlacemark(Position pstn) {
        super(pstn);
        this.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);//assuming no altitude for now.
        this.setEnableBatchPicking(false); //must be false to support the mutiselection menu(right-click menu) 
        //this.setLineEnabled(true); //Pointless for ground-based points, change when we get items with altitude

        addPropertyChangeListener(selectionListener);
    }

    @Override
    public void setDisplayName(String name) {
        this.setValue(AVKey.DISPLAY_NAME, name);
    }

    @Override
    public String getDisplayName() {
        return (String) this.getValue(AVKey.DISPLAY_NAME);
    }

    @Override
    public void setDisplayIcon(String icon) {
        this.setValue(AVKey.DISPLAY_ICON, icon);

        PointPlacemarkAttributes attr = new PointPlacemarkAttributes();
        URL url = getClass().getResource("/" + icon);
        if (url != null) {
            attr.setImageAddress(url.toString());
            attr.setImageOffset(new Offset(0.5, 0.5, AVKey.FRACTION, AVKey.FRACTION));
        } else {
            attr.setUsePointAsDefaultImage(true);
        }
        this.setAttributes(attr);
    }

    @Override
    public String getDisplayIcon() {
        return (String) this.getValue(AVKey.DISPLAY_ICON);
    }

    @Override
    public void setDescription(String desc) {
        this.setValue(AVKey.DESCRIPTION, desc);
    }

    @Override
    public String getDescription() {
        return (String) this.getValue(AVKey.DESCRIPTION);
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

    @Override
    public void onSelect() {
        //System.out.println("AdvancedPlacmark named " + getDisplayName() + " has been selected!");
    }

    @Override
    public void onHover() {
        //System.out.println("AdvancedPlacmark named " + getDisplayName() + " has been hovered!");
    }

    @Override
    public void onRollover() {
        //System.out.println("AdvancedPlacmark named " + getDisplayName() + " has been rollovered!");
    }
}
