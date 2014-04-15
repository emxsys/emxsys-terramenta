/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.globe.renderables;

import com.terramenta.globe.utilities.DateBasedVisibilitySupport;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PreRenderable;
import java.awt.Point;
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
            if (evt.getPropertyName().equals("SELECT") && evt.getNewValue() != null) {
                Point pnt = (Point) evt.getNewValue();
            } else if (evt.getPropertyName().equals("HOVER") && evt.getNewValue() != null) {
                //...
            } else if (evt.getPropertyName().equals("ROLLOVER") && evt.getNewValue() != null) {
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
        boolean isVisible = DateBasedVisibilitySupport.determineVisibility(dc, this);
        setVisible(isVisible);
    }
}
