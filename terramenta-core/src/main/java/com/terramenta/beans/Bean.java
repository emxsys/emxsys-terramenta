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
