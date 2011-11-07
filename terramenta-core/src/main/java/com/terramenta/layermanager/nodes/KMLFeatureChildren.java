/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class KMLFeatureChildren extends Index.ArrayChildren {

    private final KMLAbstractFeature container;

    public KMLFeatureChildren(KMLAbstractFeature container) {
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
                        kids = new KMLFeatureChildren(feature);
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
