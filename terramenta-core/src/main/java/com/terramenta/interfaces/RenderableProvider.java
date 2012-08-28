/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.interfaces;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Renderable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 *
 * @author chris.heidt
 */
public interface RenderableProvider extends Transferable {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(RenderableProvider.class, "RenderableProvider");

    public String getLayerName();

    public Renderable getRenderable(Position pos);
}
