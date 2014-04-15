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
import com.terramenta.layermanager.actions.LayerAddAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author heidtmare
 */
public class RootNode extends AbstractNode {

    private String ICON_PATH = "com/terramenta/layermanager/images/show-layers.png";

    /**
     *
     * @param children 
     */
    public RootNode(Children children) {
        super(children, Lookups.singleton(children));
        setName("LAYERS");
        setDisplayName("Layers");
        setIconBaseWithExtension(ICON_PATH);
    }

    /**
     * 
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(LayerAddAction.class);
    }

    /**
     * 
     * @param bln
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(LayerAddAction.class),
            SystemAction.get(DestroyNodeAction.class)
        };
        return actions;
    }
}
