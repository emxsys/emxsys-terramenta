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
package com.terramenta.interfaces;

import org.openide.util.lookup.AbstractLookup;

/**
 *
 * @author heidtmare
 */
public class Content extends AbstractLookup.Content {

    public interface Provider {

        public AbstractLookup.Content getContent();
    }
}
