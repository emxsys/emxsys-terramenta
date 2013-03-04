package com.terramenta.annotations;

import com.terramenta.drag.Draggable;
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
 */
public class DraggableAnnotation extends GlobeAnnotation implements Draggable {

    private boolean draggable = true;
    private final PropertyChangeListener selectionListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("SELECT") && evt.getNewValue().equals(Boolean.TRUE)) {
                if (evt.getSource() instanceof DraggableAnnotation) {
                    DraggableAnnotation anno = (DraggableAnnotation) evt.getSource();
                    anno.getAttributes().setHighlighted(!anno.getAttributes().isHighlighted());
                }
            }
        }
    };

    public DraggableAnnotation(String string, Position pstn) {
        super(string, pstn);
        addPropertyChangeListener(selectionListener);
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

    public boolean isVisible() {
        return this.getAttributes().isVisible();
    }

    public void setVisible(boolean vis) {
        this.getAttributes().setVisible(vis);
    }
}
