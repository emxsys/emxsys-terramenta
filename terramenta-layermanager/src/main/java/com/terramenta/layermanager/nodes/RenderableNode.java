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
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.SystemAction;

/**
 * A RenderableNode instance represents a WorldWind Renderable object.
 *
 * @author Bruce Schubert, Chris Heidt
 */
public class RenderableNode extends BeanNode implements BooleanState.Provider, Destroyable {

    private final RenderableLayer parentLayer;
    private final Renderable renderable;
    private final boolean isAVList;
    private final boolean isLayer;

    public RenderableNode(RenderableLayer parentLayer, Renderable renderable) throws IntrospectionException {
        super(renderable);
        this.parentLayer = parentLayer;
        this.renderable = renderable;

        //is the renderable ALSO an AVList?
        isAVList = (renderable instanceof AVList);
        //is the renderable ALSO a layer?
        isLayer = (renderable instanceof RenderableLayer);

        if (isAVList) {
            AVList avlRen = (AVList) renderable;
            if (avlRen.hasKey(AVKey.DISPLAY_NAME)) {
                setName(avlRen.getStringValue(AVKey.DISPLAY_NAME));
            }
            if (avlRen.hasKey(AVKey.DISPLAY_ICON)) {
                setIconBaseWithExtension(avlRen.getStringValue(AVKey.DISPLAY_ICON));
            }
        }

        if (isLayer) {
            this.setChildren(Children.create(new RenderableNodeFactory((RenderableLayer) renderable), false));
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getHtmlDisplayName() {
        if (getBooleanState()) {
            return getChildren().getNodesCount() != 0 ? getName() + " [" + getChildren().getNodesCount() + "]" : getName();
        } else {
            return "<font color='AAAAAA'><i>" + getName() + "</i></font>";
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
        //NOTE: Why were we doing this?
//        if (renderable instanceof AVList) {
//            AVList avl = (AVList) renderable;
//            Boolean visible = (Boolean) avl.getValue("VISIBLE");
//            if (visible != null) {
//                return visible;
//            }
//        }

        // layers use enabled
        if (isLayer) {
            return ((RenderableLayer) renderable).isEnabled();
        }

        //most renderables use visible
        try {
            Method isVisibleMethod = renderable.getClass().getMethod("isVisible", (Class<?>[]) null);
            return (boolean) isVisibleMethod.invoke(renderable, (Object[]) null);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            //...
        }

        return true;
    }

    @Override
    public void setBooleanState(boolean state) {
        //NOTE: Why were we doing this?
//        if (renderable instanceof AVList) {
//            AVList avl = (AVList) renderable;
//            avl.setValue("VISIBLE", state);
//            avl.firePropertyChange("VISIBLE", null, state);
//        }

        // layers use enabled
        if (isLayer) {
            ((RenderableLayer) renderable).setEnabled(state);
            this.fireIconChange();//force icon refresh
            return;
        }

        //most renderables use visible
        try {
            Method setVisibleMethod = renderable.getClass().getMethod("setVisible", new Class[]{boolean.class});
            setVisibleMethod.invoke(renderable, state);
            this.fireIconChange();//force icon refresh

            if (isAVList) {
                //this bubbles up through the parent layers and cause a redraw
                ((AVList) renderable).firePropertyChange(new PropertyChangeEvent(this, "Enabled", null, state));
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            //...
        }
    }

    @Override
    public Image getIcon(int type) {
        Image icon = super.getIcon(type);
        boolean enabled = getBooleanState();
        if (!enabled) {
            icon = ImageUtilities.createDisabledImage(icon);
        }
        return icon;
    }
}
