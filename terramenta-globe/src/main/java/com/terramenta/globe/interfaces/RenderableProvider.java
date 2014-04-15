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
package com.terramenta.globe.interfaces;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Renderable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 *
 * @author chris.heidt
 */
public abstract class RenderableProvider implements Transferable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(RenderableProvider.class, "RenderableProvider");

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DATA_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DATA_FLAVOR);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DATA_FLAVOR)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public abstract String getLayerName();

    public abstract Renderable getRenderable(Position pos);
}
