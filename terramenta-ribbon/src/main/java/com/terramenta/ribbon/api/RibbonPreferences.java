/*
 * Copyright (c) 2014, Bruce Schubert. <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the Emxsys company nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terramenta.ribbon.api;

import java.awt.Dimension;

/**
 * Interface defining the preferences used to configure the Ribbon.
 *
 * @author Bruce Schubert
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
     * Determines whether the button text is displayed with the button icon.
     *
     * @return true to always display the button text.
     */
    public boolean getAlwaysDisplayButtonText();

    /**
     * Determines whether a group's text is displayed below the buttons. Windows guidelines maintain
     * that a group's text should not be displayed when there is only one button in the group.
     *
     * @return true to always display a group's text.
     */
    public boolean getAlwaysDisplayGroupText();

    /**
     * Determines whether to display the Task Bar (mini-buttons next to application button).
     *
     * @return true to display the Task Bar (if configured).
     */
    public boolean getShouldDisplayTaskBar();

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
     * Determines whether compound buttons are allowed.  Should set to false in compact menus.
     *
     * @return true to create compound buttons
     */
    public boolean getAllowCompoundButtons();


}
