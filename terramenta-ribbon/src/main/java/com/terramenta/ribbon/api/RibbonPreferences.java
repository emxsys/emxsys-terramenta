/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License 1.0 (the "License"). You may not use this file except
 * in compliance with the License. You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0. See the License for the specific
 * language governing permissions and limitations under the License. 
 */
package com.terramenta.ribbon.api;

import java.awt.Dimension;

/**
 * Interface defining the preferences used to configure the Ribbon.
 * 
 * @author Bruce Schubert
 * @version $Id$
 */
public interface RibbonPreferences {

    /**
     * Get the LAF class defaults for the Ribbon. The following UI classes can be defined:
     *
     * "RibbonApplicationMenuButtonUI", "RibbonUI", "RibbonBandUI", "RibbonTaskToggleButtonUI",
     * "BandControlPanelUI", "CommandButtonUI", "CommandToggleButtonUI", "CommandButtonPanelUI",
     * "CommandButtonStripUI", "RibbonRootPaneUI", "RibbonComponentUI", "RibbonGalleryUI",
     * "ScrollablePanelUI", "FlowBandControlPanelUI".
     *
     * @return an array of key/value pairs, for example: return new Object[] {
     * "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.FileRibbonApplicationMenuButtonUI",
     * "RibbonUI", "com.terramenta.ribbon.FileRibbonUI" };
     *
     */
    public Object[] getLafClassDefaults();

    /**
     * Gets the preferred width and height of a Ribbon Band, which controls the overall height of
     * the Ribbon menu.
     *
     * A Dimension(width=60,height=96) will support a 32x32 icon with two lines of text.
     *
     * A Dimension(width=60,height=88) will accommodate a 32x32 icon with small fonts.
     *
     * A Dimension(width=60,height=82) will accommodates 24x24 icons with text (vs 32x32)
     *
     * A Dimension(width=40,height=60) will accommodates 32x32 icons without text.
     *
     * @return preferred width and height of a band.
     */
    public Dimension getPreferredBandSize();

    /**
     * Determines whether sub-menus are placed in a ribbon band or in a pop-up menu.
     *
     * @return true to use pop-up menus
     */
    public boolean getUsePopupMenus();

    /**
     * Determines the name of the band used for root level menu items.
     *
     * @return true to use the ribbon tab (task pane) name for the "Tasks" band.
     */
    public boolean getUseTabNameForTasksBand();

    /**
     * Determines whether the button text is displayed with the button icon.
     *
     * @return true to always display the button text.
     */
    public boolean getAlwaysDisplayButtonText();

}
