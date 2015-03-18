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
package com.terramenta.layermanager.nodes;

import gov.nasa.worldwind.ogc.kml.KMLAbstractContainer;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.util.Exceptions;

/**
 *
 * @author heidtmare
 */
public class KMLFeatureNodeFactory extends Index.ArrayChildren {

    private final KMLAbstractFeature container;

    public KMLFeatureNodeFactory(KMLAbstractFeature container) {
        super();
        this.container = container;
    }

    /**
     * 
     * @return
     */
    @Override
    protected java.util.List initCollection() {
        ArrayList childrenNodes = new ArrayList();
        if (container instanceof KMLAbstractContainer) {
            for (KMLAbstractFeature feature : ((KMLAbstractContainer) container).getFeatures()) {
                if (feature != null) {
                    Children kids = Children.LEAF;
                    if (feature instanceof KMLAbstractContainer) {
                        kids = new KMLFeatureNodeFactory(feature);
                    }
                    try {
                        childrenNodes.add(new KMLFeatureNode(feature, kids));
                    } catch (IntrospectionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        return childrenNodes;
    }
}
