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
package com.terramenta.annotations;

import com.terramenta.globe.dnd.Draggable;
import com.terramenta.globe.properties.RenderableProperties;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A draggable GlobeAnnotation
 *
 * @author Ringo Wathelet, Feb 2013
 * @author heidtmare
 */
public class DraggableAnnotation extends GlobeAnnotation implements Draggable {

    private boolean draggable = true;

    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            switch (RenderableProperties.valueOf(evt.getPropertyName())) {
                case SELECT:
                    //edit on select
                    if (evt.getNewValue() != null && evt.getSource() instanceof DraggableAnnotation) {
                        TextAnnotationEditor.edit((DraggableAnnotation) evt.getSource());
                    }
                    break;
                case VISIBLE:
                    //toggle visibility
                    //No way to toggle globe annotation visibility?!?!
                    //setVisible((boolean) evt.getNewValue());
                    break;
            }
        }
    };

    {
        addPropertyChangeListener(selectionListener);
    }

    public DraggableAnnotation(String string, Position pstn) {
        super(string, pstn);
    }

    public DraggableAnnotation(String string, Position pstn, Font font) {
        super(string, pstn, font);
    }

    public DraggableAnnotation(String string, Position pstn, Font font, Color color) {
        super(string, pstn, font, color);
    }

    public DraggableAnnotation(String string, Position pstn, AnnotationAttributes aa) {
        super(string, pstn, aa);
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
}
