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
package com.terramenta.layermanager.actions;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.globe.layers.KMLLayer;
import com.terramenta.layermanager.layerselector.LayerSelectorWizardIterator;
import com.terramenta.ribbon.RibbonActionReference;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.SurfaceImageLayer;
import gov.nasa.worldwindx.examples.util.ShapefileLoader;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Tools", id = "com.terramenta.layermanager.actions.LayerAddAction")
@ActionRegistration(
        iconBase = "com/terramenta/layermanager/images/add-layer.png",
        displayName = "#CTL_LayerAddAction",
        popupText = "Add a layer to the globe.")
@RibbonActionReference(
        path = "Ribbon/TaskPanes/LayerTools/Edit",
        position = 400,
        priority = "top",
        description = "#CTL_LayerAddAction_Hint",
        tooltipTitle = "#CTL_LayerAddAction_TooltipTitle",
        tooltipBody = "#CTL_LayerAddAction_TooltipBody",
        tooltipIcon = "com/terramenta/layermanager/images/add-layer32.png",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_LayerAddAction=Add Layer",
    "CTL_LayerAddAction_Hint=Add a layer to the globe.",
    "CTL_LayerAddAction_TooltipTitle=Add a Layer",
    "CTL_LayerAddAction_TooltipBody=Launches a wizard to add a KML, Shapefile, GeoTiff or WMS layer to the globe.",})
public class LayerAddAction extends SystemAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        LayerSelectorWizardIterator iterator = new LayerSelectorWizardIterator();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(iterator);
        iterator.initialize(wizardDescriptor);
        //{0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
        // {1} will be replaced by WizardDescriptor.Iterator.name()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0} ({1})"));
        wizardDescriptor.setTitle("Layer Selector");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            Object layerType = wizardDescriptor.getProperty("layerType");
            Object layerPath = wizardDescriptor.getProperty("layerPath");
            if (layerType != null && layerPath != null) {
                Layer layer = null;

                try {
                    if (layerType.equals("KML")) {
                        layer = new KMLLayer((String) layerPath);
                    } else if (layerType.equals("Shapefile")) {
                        ShapefileLoader loader = new ShapefileLoader();
                        layer = (Layer) loader.createLayerFromSource(new URL((String) layerPath));
                    } else if (layerType.equals("GeoTiff")) {
                        SurfaceImageLayer sil = new SurfaceImageLayer();
                        sil.addImage((String) layerPath);
                        layer = sil;
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

                if (layer != null) {
                    Lookup.getDefault().lookup(WorldWindManager.class).getLayers().add(layer);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return Bundle.CTL_LayerAddAction();
    }

    /**
     *
     * @return
     */
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
