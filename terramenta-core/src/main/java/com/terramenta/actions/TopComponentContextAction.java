/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import org.openide.windows.TopComponent;

/**
 *
 * @author Chris.Heidt
 */
public abstract class TopComponentContextAction extends AbstractAction implements PropertyChangeListener {

    private final Class<? extends TopComponent> clazz;

    public TopComponentContextAction(Class<? extends TopComponent> clazz) {
        this.clazz = clazz;
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }

    /**
     * Toggle enabled and disabled based on if TopComponent opens/closes
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_OPENED) || evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_CLOSED)) {
            //check is the evt is for the specified TopComponent type
            if (clazz.isInstance(evt.getNewValue())) {
                TopComponent tc = (TopComponent) evt.getNewValue();
                setEnabled(tc.isOpened());
            }
        }
    }
}
