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
package com.terramenta.globe.dnd;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.globe.interfaces.RenderableProvider;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Point;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author chris.heidt
 */
public class RenderableDropTargetListener implements DropTargetListener {

    private static final Logger LOGGER = Logger.getLogger(RenderableDropTargetListener.class.getName());
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    @Override
    public void dragExit(DropTargetEvent dtde) {
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        if (dtde.isDataFlavorSupported(RenderableProvider.DATA_FLAVOR)) {
            try {
                Object transData = dtde.getTransferable().getTransferData(RenderableProvider.DATA_FLAVOR);
                if (transData instanceof RenderableProvider) {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                    //calculate position
                    Point p = dtde.getLocation();
                    Position pos = wwm.getWorldWindow().getView().computePositionFromScreenPoint(p.getX(), p.getY());
                    //Position posNoElev = new Position(pos.getLatitude(), pos.getLongitude(), 0d);

                    //build the renderable
                    RenderableProvider rp = (RenderableProvider) dtde.getTransferable().getTransferData(RenderableProvider.DATA_FLAVOR);
                    Renderable r = rp.getRenderable(pos);
                    if (r != null) {
                        //get the layer
                        String ln = rp.getLayerName();
                        RenderableLayer layer = (RenderableLayer) wwm.getLayers().getLayerByName(ln);
                        if (layer == null) {
                            layer = new RenderableLayer();
                            layer.setName(ln);
                            wwm.getLayers().add(layer);
                        }

                        //add the renderable
                        layer.addRenderable(r);
                    }
                }
            } catch (UnsupportedFlavorException ufe) {
                Exceptions.printStackTrace(ufe);
                dtde.rejectDrop();
                dtde.dropComplete(true);
            } catch (IOException ioe) {
                Exceptions.printStackTrace(ioe);
                dtde.rejectDrop();
                dtde.dropComplete(false);
            }
        } else {
            LOGGER.warning("Unsupported Data Flavor");
            dtde.rejectDrop();
            dtde.dropComplete(false);
        }
    }
}
