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
