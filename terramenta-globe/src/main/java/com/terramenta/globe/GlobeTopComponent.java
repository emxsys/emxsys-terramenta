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
package com.terramenta.globe;

import com.terramenta.globe.dnd.RenderableDropTargetListener;
import com.terramenta.ribbon.RibbonActionReference;
import java.awt.Component;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@TopComponent.Description(
        preferredID = "GlobeTopComponent",
        iconBase = "com/terramenta/globe/images/show-globe.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(
        mode = "editor",
        openAtStartup = true)
@ActionID(
        category = "Window",
        id = "com.terramenta.globe.GlobeTopComponent")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_GlobeAction",
        preferredID = "GlobeTopComponent")
@ActionReference(path = "Menu/Window/Show")
@RibbonActionReference(
        path = "Menu/Window/Show",
        position = 1,
        priority = "top",
        description = "#CTL_GlobeAction_Hint",
        tooltipTitle = "#CTL_GlobeAction_TooltipTitle",
        tooltipBody = "#CTL_GlobeAction_TooltipBody",
        tooltipIcon = "com/terramenta/globe/images/show-globe32.png",
        tooltipFooter = "#CTL_GlobeAction_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_GlobeTopComponent=Globe",
    "CTL_GlobeTopComponent_Hint=This is the Globe window.",
    "CTL_GlobeAction=Globe",
    "CTL_GlobeAction_Hint=Show the Globe window.",
    "CTL_GlobeAction_TooltipTitle=Show Globe",
    "CTL_GlobeAction_TooltipBody=Activates the Globe window and displays the 3D virtual earth.",
    "CTL_GlobeAction_TooltipFooter=Press F1 for more help."
})
public class GlobeTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    //fetching gcm just to ensure it has been constructed
    private static final GlobeContentManager gcm = Lookup.getDefault().lookup(GlobeContentManager.class);
    private ExplorerManager explorerManager;
    private Lookup explorerLookup;

    /**
     *
     */
    public GlobeTopComponent() {
        setName(NbBundle.getMessage(GlobeTopComponent.class, "CTL_GlobeTopComponent"));
        setToolTipText(NbBundle.getMessage(GlobeTopComponent.class, "CTL_GlobeTopComponent_Hint"));

        //setup DnD
        initDnD();

        initComponents();

        //setup lookups
        initExplorerManager();
    }

    private void initDnD() {
        DropTarget dt = new DropTarget(this, new RenderableDropTargetListener());
        dt.setDefaultActions(DnDConstants.ACTION_COPY_OR_MOVE);
        dt.setActive(true);
    }

    private void initExplorerManager() {
        // Standard boilerplate to setup an explorer manager
        explorerManager = new ExplorerManager();
        explorerLookup = ExplorerUtils.createLookup(explorerManager, getActionMap());
        // Associate our WW Mananger lookup(s) with the top component's lookup.
        associateLookup(new ProxyLookup(
                explorerLookup,
                wwm.getLookup()
        ));
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel globePanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        globePanel.setBackground(new java.awt.Color(0, 0, 0));
        globePanel.setLayout(new java.awt.BorderLayout());
        globePanel.add((Component)wwm.getWorldWindow(), java.awt.BorderLayout.CENTER);
        add(globePanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     *
     */
    @Override
    protected void componentActivated() {
        super.componentActivated();
        Component comp = (Component) wwm.getWorldWindow();
        comp.requestFocusInWindow();
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    public static boolean hasOpenInstance() {
        TopComponent tc = WindowManager.getDefault().findTopComponent("GlobeTopComponent");
        return (tc != null && tc.isOpened());
    }
}
