/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

/**
 *
 * @author heidtmare
 */
public interface Position {

    public interface Provider {

        public gov.nasa.worldwind.geom.Position getPosition();

        public void setPosition(gov.nasa.worldwind.geom.Position position);
    }
}
