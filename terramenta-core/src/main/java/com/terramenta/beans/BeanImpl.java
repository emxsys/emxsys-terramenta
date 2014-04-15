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
package com.terramenta.beans;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.AbstractLookup.Content;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author heidtmare
 */
public class BeanImpl implements Bean {

    private final Lookup lookup;
    private final PropertyChangeSupport propertyChangeSupport;
    private final Content content;

    public BeanImpl() {
        content = new InstanceContent();
        lookup = new AbstractLookup(content);
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public Content getContent() {
        return content;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}
