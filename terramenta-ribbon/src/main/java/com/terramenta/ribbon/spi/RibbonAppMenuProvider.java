/*
 * Copyright (c) 2010 Chris Böhme - Pinkmatter Solutions. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.LayerRibbonAppMenuProvider;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;

/**
 * Class which provides the application or start menu
 * @author Chris
 */
public abstract class RibbonAppMenuProvider {

    public abstract RibbonApplicationMenu createApplicationMenu();

    /**
     * Create the toolTip for the application menu.
     * @return RichtoolTip for application menu
     */
    public RichTooltip createApplicationMenuTooltip() {
        RichTooltip tooltip = new RichTooltip();
        tooltip.setTitle(NbBundle.getMessage(LayerRibbonAppMenuProvider.class, "LBL_AppMenuTitle"));// NOI18N
        tooltip.addDescriptionSection(NbBundle.getMessage(LayerRibbonAppMenuProvider.class, "HINT_AppMenu"));// NOI18N
        tooltip.setMainImage(ImageUtilities.loadImage("images/app-menu.png", true));// NOI18N
        tooltip.setFooterImage(ImageUtilities.loadImage("images/help.png", true));// NOI18N
        tooltip.addFooterSection(NbBundle.getMessage(LayerRibbonAppMenuProvider.class, "HINT_AppMenuHelp"));// NOI18N
        return tooltip;
    }

    protected RibbonApplicationMenuEntryPrimary.PrimaryRolloverCallback createPrimaryRolloverCallback() {
        return null;
    }

    /**
     * Finds the default RibbonAppMenuProvider
     * @return the default RibbonAppMenuProvider
     */
    public static RibbonAppMenuProvider getDefault() {
        RibbonAppMenuProvider provider = Lookup.getDefault().lookup(RibbonAppMenuProvider.class);
        if (provider == null) {
            provider = new LayerRibbonAppMenuProvider();
        }
        return provider;
    }
}
