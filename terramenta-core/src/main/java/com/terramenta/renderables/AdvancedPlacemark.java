/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.interfaces.ContextualObject;
import com.terramenta.interfaces.SelectableObject;
import com.terramenta.interfaces.TemporalObject;
import com.terramenta.time.DateProvider;
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
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import org.openide.util.Lookup;

/**
 *
 * @author chris.heidt
 */
public class AdvancedPlacemark extends PointPlacemark implements ContextualObject, TemporalObject, SelectableObject {

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
