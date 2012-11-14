/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.drag;

import gov.nasa.worldwind.geom.Position;

/**
 *
 * @author Chris.Heidt
 */
public interface Draggable {

    public boolean isDraggable();

    public void setDraggable(boolean draggable);

    public Position getPosition();

    public void setPosition(Position position);
}
