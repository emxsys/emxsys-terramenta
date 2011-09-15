/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.globe.layers;

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
