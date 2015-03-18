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

import com.terramenta.actions.DestroyNodeAction;
import com.terramenta.actions.ToggleNodeAction;
import com.terramenta.interfaces.BooleanState;
import com.terramenta.interfaces.Destroyable;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import java.beans.IntrospectionException;
import javax.swing.Action;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/**
 * A RenderableNode instance represents a WorldWind Renderable object.
 *
 * @author Bruce Schubert, Chris Heidt
 */
public class RenderableNode extends BeanNode implements BooleanState.Provider, Destroyable {

    private final RenderableLayer parentLayer;
    private final Renderable renderable;

    public RenderableNode(RenderableLayer parentLayer, Renderable renderable) throws IntrospectionException {
        super(renderable);
        this.parentLayer = parentLayer;
        this.renderable = renderable;
        
        //is the renderable ALSO a layer?
        if (renderable instanceof RenderableLayer) {
            this.setChildren(Children.create(new RenderableNodeFactory((RenderableLayer) renderable), false));
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(ToggleNodeAction.class);
    }

    /**
     *
     * @param bln ignored
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(RenameAction.class),
            SystemAction.get(ToggleNodeAction.class),
            SystemAction.get(DestroyNodeAction.class),
            null,
            SystemAction.get(PropertiesAction.class)
        };
        return actions;
    }

    @Override
    public void doDestroy() {
        parentLayer.removeRenderable(renderable);
        parentLayer.firePropertyChange("Renderables", null, parentLayer.getRenderables());
    }

    @Override
    public boolean getBooleanState() {
        if (renderable instanceof AVList) {
            AVList avl = (AVList) renderable;
            Boolean visible = (Boolean) avl.getValue("VISIBLE");
            if (visible != null) {
                return visible;
            }
        }
        return true;
    }

    @Override
    public void setBooleanState(boolean state) {
        if (renderable instanceof AVList) {
            AVList avl = (AVList) renderable;
            avl.setValue("VISIBLE", state);
            avl.firePropertyChange("VISIBLE", null, state);
        }
    }

}
