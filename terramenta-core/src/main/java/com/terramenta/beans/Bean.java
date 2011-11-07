/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.beans;

import com.terramenta.interfaces.Content;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
public interface Bean extends Content.Provider, Lookup.Provider, Serializable {

    /**
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);
}
