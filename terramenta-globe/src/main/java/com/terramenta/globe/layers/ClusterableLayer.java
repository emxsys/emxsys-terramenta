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

import com.terramenta.globe.interfaces.Clusterable;
import com.terramenta.globe.utilities.ClusterUtilities;
import com.terramenta.globe.utilities.ClusterUtilities.ClusterTreeNode;
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
