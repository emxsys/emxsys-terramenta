/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.options;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
@OptionsPanelController.TopLevelRegistration(categoryName = "#OptionsCategory_Name_Globe", iconBase = "com/terramenta/globe/images/globeBlue32.png",
        keywords = "#OptionsCategory_Keywords_Globe",
        keywordsCategory = "Globe",
        position = 1000,
        id = "Globe")
public final class GlobeOptionsPanelController extends OptionsPanelController {

    private GlobeOptionsPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     *
     */
    @Override
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     *
     */
    @Override
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    /**
     *
     */
    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     *
     * @return
     */
    @Override
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /**
     *
     * @param masterLookup
     * @return
     */
    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /**
     *
     * @param l
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     *
     * @param l
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private GlobeOptionsPanel getPanel() {
        if (panel == null) {
            panel = new GlobeOptionsPanel();
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }
}
