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
package com.terramenta.layermanager.ribbons;

import com.terramenta.layermanager.nodes.LayerNode;
import com.terramenta.ribbon.RibbonManager;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonContextualTaskGroup;

/**
 * The LayerTools instance listens for the existence of a LayerNode object on the lookup, and if
 * found it displays the "Layer Tools" contextual task group.
 * <p>
 * @author Bruce Schubert
 */
@Messages({
    "CTL_LayerTools=Layer Tools"
})
public final class LayerTools implements LookupListener {

    private RibbonContextualTaskGroup taskGroup;
    private final Lookup.Result<LayerNode> lookupResult;

    private LayerTools() {
        // Initilize the lookup listener
        this.lookupResult = Utilities.actionsGlobalContext().lookupResult(LayerNode.class);
        this.lookupResult.addLookupListener(this);

        // Trigger initial show/hide
        resultChanged(null);
    }

    /**
     * Gets the "Layer Tools" task group.
     * @return The RibbonContextualTaskGroup for the Layer Tools.
     */
    private RibbonContextualTaskGroup getLayerToolsTaskGroup() {
        if (this.taskGroup == null) {
            JRibbon ribbon = Lookup.getDefault().lookup(RibbonManager.class).getRibbon();
            if (ribbon == null) {
                throw new IllegalStateException("Ribbon cannot be null.");
            }
            // Find the Layer Tools task group
            for (int i = 0; i < ribbon.getContextualTaskGroupCount(); i++) {
                RibbonContextualTaskGroup group = ribbon.getContextualTaskGroup(i);
                if (group.getTitle().equalsIgnoreCase(Bundle.CTL_LayerTools())) {
                    this.taskGroup = group;
                }
            }
        }
        return this.taskGroup;
    }

    /**
     * Enables the task group if a single layer is in the lookup.
     *
     * @param ev ignored
     */
    @Override
    public void resultChanged(LookupEvent ev) {
        RibbonContextualTaskGroup group = getLayerToolsTaskGroup();
        if (group != null) {
            JRibbon ribbon = Lookup.getDefault().lookup(RibbonManager.class).getRibbon();
            // Enable and show the task pane when a single layer is selected.
            if (this.lookupResult.allInstances().size() == 1) {
                ribbon.setVisible(group, true);                
                ribbon.setSelectedTask(group.getTask(0)); // use the first (and only) task.
            } else {
                ribbon.setVisible(group, false);
            }
        }
    }

    /**
     * Gets the singleton instance.
     *
     * @return singleton
     */
    public static LayerTools getInstance() {
        return LayerToolsHolder.INSTANCE;
    }

    private static class LayerToolsHolder {

        private static final LayerTools INSTANCE = new LayerTools();
    }

}
