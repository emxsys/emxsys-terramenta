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
package com.terramenta.globe.layers;

import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.KMLRoot;
import gov.nasa.worldwind.ogc.kml.impl.KMLController;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.openide.util.Exceptions;

/**
 *
 * @author heidtmare
 */
public class KMLLayer extends RenderableLayer {

    private KMLController kmlController;

    public KMLLayer(String path) {
        try {
            KMLRoot kmlRoot = KMLRoot.createAndParse(path);
            kmlController = new KMLController(kmlRoot);
            setName(kmlRoot.getFeature().getName());
            addRenderable(kmlController);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (XMLStreamException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * @return the kmlController
     */
    public KMLController getKmlController() {
        return kmlController;
    }
}
