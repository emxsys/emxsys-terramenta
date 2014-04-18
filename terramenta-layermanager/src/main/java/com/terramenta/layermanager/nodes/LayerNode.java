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

import com.terramenta.globe.WorldWindManager;
import com.terramenta.globe.layers.KMLLayer;
import com.terramenta.interfaces.BooleanState;
import com.terramenta.interfaces.Destroyable;
import com.terramenta.actions.DestroyNodeAction;
import com.terramenta.actions.ToggleNodeAction;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author heidtmare
 */
public class LayerNode extends BeanNode implements BooleanState.Provider, Destroyable, PropertyChangeListener {

    private String ENABLED_ICON_BASE = "com/terramenta/layermanager/images/bulletGreen.png";
    private String DISABLED_ICON_BASE = "com/terramenta/layermanager/images/bulletBlack.png";

    /**
     *
     * @param layer
     * @throws IntrospectionException
     */
    public LayerNode(Layer layer) throws IntrospectionException {
        super(layer);
        this.setIconBaseWithExtension(layer.isEnabled() ? ENABLED_ICON_BASE : DISABLED_ICON_BASE);
        this.setSynchronizeName(true);
        if (layer instanceof KMLLayer) {
            this.setChildren(new KMLFeatureChildren(((KMLLayer) layer).getKmlController().getKmlRoot().getFeature()));
        } else if (layer instanceof RenderableLayer) {
            //this.setChildren(new LayerChildren((RenderableLayer) layer));
            this.setChildren(Children.create(new RenderableChildFactory((RenderableLayer) layer), false));
        }

        layer.addPropertyChangeListener(WeakListeners.propertyChange(this, layer));
    }

    /**
     *
     * @return
     */
    @Override
    public String getHtmlDisplayName() {
        Layer layer = (Layer) getBean();
        if (layer.isEnabled()) {
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
     * @param bln
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class),
            null,
            SystemAction.get(RenameAction.class),
            SystemAction.get(ToggleNodeAction.class),
            SystemAction.get(DestroyNodeAction.class),
            null,
            SystemAction.get(PropertiesAction.class)
        };
        return actions;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "Enabled":
                if (evt.getNewValue().equals(Boolean.TRUE)) {
                    this.setIconBaseWithExtension(ENABLED_ICON_BASE);
                } else {
                    this.setIconBaseWithExtension(DISABLED_ICON_BASE);
                }   this.fireDisplayNameChange(null, getDisplayName());
                break;
            case "Renderables":
                this.fireDisplayNameChange(null, getDisplayName());
                break;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean canCut() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public boolean getBooleanState() {
        return ((Layer) getBean()).isEnabled();
    }

    @Override
    public void setBooleanState(boolean state) {
        ((Layer) getBean()).setEnabled(state);
    }

    @Override
    public void doDestroy() {
        WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
        wwm.getLayers().remove((Layer) getBean());
    }
}
