/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.layermanager;

import com.qna.terramenta.layermanager.nodes.RootChildren;
import com.qna.terramenta.layermanager.nodes.RootNode;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.qna.terramenta.layermanager//LayerManager//EN",
autostore = false)
@TopComponent.Description(preferredID = "LayerManagerTopComponent",
iconBase = "images/layers.png",
persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "rightSide", openAtStartup = true)
@ActionID(category = "Window", id = "com.qna.terramenta.layermanager.LayerManagerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_LayerManagerAction",
preferredID = "LayerManagerTopComponent")
public final class LayerManagerTopComponent extends TopComponent implements ExplorerManager.Provider {

    private final ExplorerManager em = new ExplorerManager();

    /**
     * 
     */
    public LayerManagerTopComponent() {
        initComponents();
        setName(NbBundle.getMessage(LayerManagerTopComponent.class, "CTL_LayerManagerTopComponent"));
        setToolTipText(NbBundle.getMessage(LayerManagerTopComponent.class, "HINT_LayerManagerTopComponent"));

        associateLookup(ExplorerUtils.createLookup(this.getExplorerManager(), this.getActionMap()));
        em.setRootContext(new RootNode(new RootChildren()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
     */
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    /**
     * 
     */
    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    /**
     * 
     * @return
     */
    @Override
    public ExplorerManager getExplorerManager() {
        return em;
    }
}
