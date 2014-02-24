/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.terramenta.layermanager;

import com.terramenta.layermanager.nodes.RootChildren;
import com.terramenta.layermanager.nodes.RootNode;
import com.terramenta.layermanager.ribbons.LayerTools;
import com.terramenta.layermanager.ribbons.AnnotationTools;
import com.terramenta.ribbon.RibbonActionReference;
import org.openide.awt.ActionID;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@TopComponent.Description(preferredID = "LayerManagerTopComponent", iconBase = "images/layers.png", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "rightSide", openAtStartup = true)
@ActionID(category = "Window", id = "com.terramenta.layermanager.LayerManagerTopComponent")
@RibbonActionReference(path = "Menu/Window/Show",
        position = 2,
        priority = "top",
        description = "#CTL_LayerManagerAction_Hint",
        tooltipTitle = "#CTL_LayerManagerAction_TooltipTitle",
        tooltipBody = "#CTL_LayerManagerAction_TooltipBody",
        tooltipIcon = "images/layers32.png",
        tooltipFooter = "#CTL_LayerManagerAction_TooltipFooter",
        tooltipFooterIcon = "images/help.png")
@NbBundle.Messages(
        {
            "CTL_LayerManagerTopComponent=Layer Manager",
            "CTL_LayerManagerTopComponent_Hint=This is the Layer Manager.",
            "CTL_LayerManagerAction=Layer Manager",
            "CTL_LayerManagerAction_Hint=Show the Layer Manager.",
            "CTL_LayerManagerAction_TooltipTitle=Show Layer Manager",
            "CTL_LayerManagerAction_TooltipBody=Activates the Layer Manager window used for managing "
                    + "the layers displayed on the Globe.",
            "CTL_LayerManagerAction_TooltipFooter=Press F1 for more help."
        })

@TopComponent.OpenActionRegistration(displayName = "#CTL_LayerManagerAction", preferredID = "LayerManagerTopComponent")
public final class LayerManagerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager em = new ExplorerManager();

    /**
     *
     */
    public LayerManagerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(LayerManagerTopComponent.class, "CTL_LayerManagerTopComponent"));
        setToolTipText(NbBundle.getMessage(LayerManagerTopComponent.class, "CTL_LayerManagerTopComponent_Hint"));

        associateLookup(ExplorerUtils.createLookup(this.getExplorerManager(), this.getActionMap()));
        em.setRootContext(new RootNode(new RootChildren()));

        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                // Initialize contextual ribbon task groups
                LayerTools.getInstance();
                AnnotationTools.getInstance();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane layerTree = new BeanTreeView();

        setLayout(new java.awt.BorderLayout());
        add(layerTree, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    /**
     *
     * @return
     */
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
}
