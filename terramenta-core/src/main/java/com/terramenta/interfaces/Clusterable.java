/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

import gov.nasa.worldwind.geom.Position;

/**
 * Identity interface for objects that can be clustered.
 * @see com.terramenta.globe.layers.ClusterableLayer
 * @author chris.heidt
 */
public interface Clusterable {

    public Position getPosition();
}
