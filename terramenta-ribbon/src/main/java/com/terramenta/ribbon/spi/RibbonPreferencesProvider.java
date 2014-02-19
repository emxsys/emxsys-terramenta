/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.ribbon.spi;

import java.awt.Dimension;
import org.openide.util.Lookup;


/**
 * RibbonPreferencesProvider is an service provider interface used by the application for injecting
 * its ribbon configuration into the RibbonCompontentProvider.
 *
 * @author Bruce Schubert
 * @version $Id$
 */
public abstract class RibbonPreferencesProvider
{

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
    public abstract Object[] getLafClassDefaults();


    /**
     * Gets the preferred width and height of a Ribbon Band, which controls the overall height of
     * the Ribbon menu.
     *
     * @return preferred width and height of a band.
     */
    public abstract Dimension getPreferredBandSize();


    /**
     * Determines whether sub-menus are placed in a ribbon band or in a pop-up menu.
     *
     * @return true to use pop-up menus
     */
    public abstract boolean getUsePopupMenus();


    /**
     * Determines the name of the band used for root level menu items.
     *
     * @return true to use the ribbon tab (task pane) name for the "Tasks" band.
     */
    public abstract boolean getUseTabNameForTasksBand();


    public static RibbonPreferencesProvider getDefault()
    {
        RibbonPreferencesProvider provider = Lookup.getDefault().lookup(RibbonPreferencesProvider.class);
        if (provider == null)
        {
            provider = new DefaultRibbonPreferencesProvider();
        }
        return provider;
    }


    /**
     * Creates the preferences for the Terramenta application.
     */
    private final static class DefaultRibbonPreferencesProvider extends RibbonPreferencesProvider
    {

        @Override
        public Object[] getLafClassDefaults()
        {
            return new Object[]
            {
                "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.FileRibbonApplicationMenuButtonUI",
                "RibbonUI", "com.terramenta.ribbon.FileRibbonUI",
            };
        }


        @Override
        public Dimension getPreferredBandSize()
        {
            return new Dimension(40, 60);
        }


        @Override
        public boolean getUsePopupMenus()
        {
            return true;
        }


        @Override
        public boolean getUseTabNameForTasksBand()
        {
            return true;
        }

    }

}
