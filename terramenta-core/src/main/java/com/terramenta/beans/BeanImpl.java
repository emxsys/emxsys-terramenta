/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
