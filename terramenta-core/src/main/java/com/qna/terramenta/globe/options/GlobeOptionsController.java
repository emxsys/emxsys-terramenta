/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.globe.options;

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
@OptionsPanelController.SubRegistration(location = "Advanced",
displayName = "#AdvancedOption_DisplayName_Globe",
keywords = "#AdvancedOption_Keywords_Globe",
keywordsCategory = "Advanced/Globe")
public final class GlobeOptionsController extends OptionsPanelController {

    private GlobeOptions panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    /**
     * 
     */
    public void update() {
        getPanel().load();
        changed = false;
    }

    /**
     * 
     */
    public void applyChanges() {
        getPanel().store();
        changed = false;
    }

    /**
     * 
     */
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    /**
     * 
     * @return
     */
    public boolean isValid() {
        return getPanel().valid();
    }

    /**
     * 
     * @return
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * 
     * @return
     */
    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    /**
     * 
     * @param masterLookup
     * @return
     */
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    /**
     * 
     * @param l
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    /**
     * 
     * @param l
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private GlobeOptions getPanel() {
        if (panel == null) {
            panel = new GlobeOptions();
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
