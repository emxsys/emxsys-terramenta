/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.layers;

import com.terramenta.interfaces.Clusterable;
import com.terramenta.utilities.ClusterUtilities;
import com.terramenta.utilities.ClusterUtilities.ClusterTreeNode;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Renderable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author chris.heidt
 */
public class ClusterableLayer extends RenderableLayer {

    private static final Logger LOGGER = Logger.getLogger(ClusterableLayer.class.getName());

    @Override
    protected void doPreRender(DrawContext dc, Iterable<? extends Renderable> itrbl) {
        super.doPreRender(dc, itrbl);

        List<Clusterable> clstrbls = new ArrayList<Clusterable>();
        for (Renderable r : itrbl) {
            if (r instanceof Clusterable) {
                clstrbls.add((Clusterable) r);
            }
        }

        if (clstrbls.isEmpty()) {
            return;
        }

        ClusterTreeNode node = ClusterUtilities.cluster(clstrbls);
        if (node == null) {
            return;
        }

        //TODO: make cluser renderables
    }

    @Override
    protected void doRender(DrawContext dc, Iterable<? extends Renderable> itrbl) {
        super.doRender(dc, itrbl);
    }
}
