package com.terramenta.annotations;

import com.terramenta.drag.Draggable;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.GlobeAnnotation;
import java.awt.Color;
import java.awt.Font;

/**
 * A draggable GlobeAnnotation
 * 
 * @author Ringo Wathelet, Feb 2013
 */
public class DraggableAnnotation extends GlobeAnnotation implements Draggable {

    private boolean draggable = true;

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

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

}
