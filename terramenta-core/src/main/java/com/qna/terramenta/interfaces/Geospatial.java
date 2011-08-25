/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.interfaces;

import gov.nasa.worldwind.geom.Position;

/**
 *
 * @author heidtmare
 */
public interface Geospatial {

    /**
     * 
     * @return
     */
    public Position getPosition();

    /**
     * 
     * @param coord
     */
    public void setPosition(Position coord);
}
