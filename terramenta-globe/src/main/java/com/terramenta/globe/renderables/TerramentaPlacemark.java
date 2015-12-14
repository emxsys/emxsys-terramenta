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

import com.terramenta.globe.properties.RenderableProperties;
import com.terramenta.globe.utilities.RenderUtilities;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.BasicWWTexture;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.WWTexture;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.time.Instant;

/**
 * Example of a placemark with selection and temporal support
 *
 * @author chris.heidt
 */
public class TerramentaPlacemark extends PointPlacemark {

    private static final PropertyChangeListener selectionListener = (PropertyChangeEvent evt) -> {
        if (evt.getPropertyName().equals(RenderableProperties.SELECT.toString()) && evt.getNewValue() != null) {
            Point screenPoint = (Point) evt.getNewValue();
            //Here you would react to the selection
        } else if (evt.getPropertyName().equals(RenderableProperties.HOVER.toString()) && evt.getNewValue() != null) {
            //...
        } else if (evt.getPropertyName().equals(RenderableProperties.ROLLOVER.toString()) && evt.getNewValue() != null) {
            //...
        }
    };

    /**
     * If temporal check is enabled we determine if display date is within the current display
     * interval
     */
    private boolean enableTemporalVisibilityCheck = true;

    public TerramentaPlacemark(Position pstn, Object bean) {
        super(pstn);
        setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        setEnableBatchPicking(false);

        setBean(bean);
        setDisplayName(bean.getClass().getSimpleName());//replace with some info from your bean
        setDescription(bean.getClass().getName());//replace with some info from your bean
        setDisplayDatetime(Instant.now());//replace with some info from your bean

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

    public void setDisplayDatetime(Instant datetime) {
        setValue("DISPLAY_DATETIME", datetime);
    }

    public boolean isEnableTemporalVisibilityCheck() {
        return enableTemporalVisibilityCheck;
    }

    public void setEnableTemporalVisibilityCheck(boolean enabled) {
        this.enableTemporalVisibilityCheck = enabled;
    }

    @Override
    public void render(DrawContext dc) {
        //If temporal check is enabled we determine if display date is within the current display interval
        if (enableTemporalVisibilityCheck && !RenderUtilities.determineTemporalVisibility(dc, this)) {
            return;
        }
        super.render(dc);
    }

    /**
     * fixes a wwj bug in 2.0.0
     *
     * @param address
     * @return
     */
    @Override
    protected WWTexture initializeTexture(String address) {
        URL localUrl = WorldWind.getDataFileStore().requestFile(address);
        return localUrl == null ? null : new BasicWWTexture(localUrl, true);
    }
}
