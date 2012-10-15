/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.renderables;

import com.terramenta.time.DateInterval;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PreRenderable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 * Example of tying selection and temporal support to a placemark
 *
 * @author chris.heidt
 */
public class TerramentaPlacemark extends PointPlacemark implements PreRenderable {

    private static final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("SELECT") && evt.getNewValue().equals(Boolean.TRUE)) {
                //...
            } else if (evt.getPropertyName().equals("HOVER") && evt.getNewValue().equals(Boolean.TRUE)) {
                //...
            } else if (evt.getPropertyName().equals("ROLLOVER") && evt.getNewValue().equals(Boolean.TRUE)) {
                //...
            }
        }
    };

    public TerramentaPlacemark(Position pstn, Object bean) {
        super(pstn);
        setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        setEnableBatchPicking(false);

        setBean(bean);
        setDisplayName(bean.getClass().getSimpleName());//replace with some info from your bean
        setDescription(bean.getClass().getName());//replace with some info from your bean
        setDisplayDate(new Date());//replace with some info from your bean

        addPropertyChangeListener(selectionListener);
    }

    public void setBean(Object bean) {
        setValue("bean", bean);
    }

    public void setDisplayName(String name) {
        setValue(AVKey.DISPLAY_NAME, name);
    }

    public void setDescription(String desc) {
        setValue(AVKey.DESCRIPTION, desc);
    }

    public void setDisplayDate(Date date) {
        setValue("DISPLAY_DATE", date);
    }

    @Override
    public void preRender(DrawContext dc) {
        //the drawcontext contains the currect display date as well as
        //the date interval derived from the current linger setting
        if (dc != null && this.hasKey("DISPLAY_DATE") && dc.hasKey("DISPLAY_DATEINTERVAL")) {
            Date displayDate = (Date) this.getValue("DISPLAY_DATE");
            DateInterval displayDateInterval = (DateInterval) dc.getValue("DISPLAY_DATEINTERVAL");

            //does this renderable exist within the interval?
            long displayDateMillis = displayDate.getTime();
            boolean isWithin = (displayDateMillis >= displayDateInterval.getStartMillis() && displayDateMillis <= displayDateInterval.getEndMillis()) ? true : false;
            setVisible(isWithin);
        }
    }
}
