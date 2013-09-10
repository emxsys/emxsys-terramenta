/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

import gov.nasa.worldwind.geom.Position;

/**
 *
 * @author heidtmare
 */
public interface PositionProvider {

    public Position getPosition();

    public void setPosition(Position position);
}
