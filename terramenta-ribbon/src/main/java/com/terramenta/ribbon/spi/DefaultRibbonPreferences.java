/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.spi;

import java.awt.Dimension;

/**
 * Default Terramenta ribbon preferences providing a shorter ribbon without button labels.
 * 
 * @author Bruce Schubert
 */
public class DefaultRibbonPreferences extends Office2013RibbonPreferences {


    /**
     * Use the Office 2013 LAF class defaults and colors, but override the size and text settings.
     */
    public DefaultRibbonPreferences()
    {
        super();
        setPreferredBandSize(new Dimension(40, 60));// Shorter size supports buttons without text
        setAlwaysDisplayButtonText(false);
        setUsePopupMenus(true);
        setUseTabNameForTasksBand(false);
        
    }

    

}
