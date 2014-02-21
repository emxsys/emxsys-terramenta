/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.ribbon.spi;

import com.terramenta.ribbon.api.RibbonPreferences;
import java.awt.Dimension;


/**
 * BasicRibbonPreferences provides an entry point for Ribbon customizations. Customized preferences
 * can be derived from this class, which will ensure there is always a basic value for any
 * RibbonPreference property.
 *
 * @author Bruce Schubert
 */
public class BasicRibbonPreferences implements RibbonPreferences
{

    private Object[] lafClassDefaults;
    private Dimension preferredBandSize;
    private boolean shouldDisplayTaskBar = true;
    private boolean usePopupMenus = true;
    private boolean useTabNameForTasksBand = true;      // true = original Terramenta style
    private boolean alwaysDisplayButtonText = false;    // true = original Terramenta style


    @Override
    public Object[] getLafClassDefaults()
    {
        if (lafClassDefaults == null)
        {
            lafClassDefaults = new Object[]
            {
                // UI Classes
                "RibbonApplicationMenuButtonUI", "com.terramenta.ribbon.NbRibbonApplicationMenuButtonUI",
            };
        }
        return lafClassDefaults;

    }


    @Override
    public Dimension getPreferredBandSize()
    {
        if (preferredBandSize == null)
        {
            preferredBandSize = new Dimension(40, 60); // Original Terramenta short ribbon without text
        }
        return preferredBandSize;
    }


    @Override
    public boolean getAlwaysDisplayButtonText()
    {
        return alwaysDisplayButtonText;
    }


    @Override
    public boolean getShouldDisplayTaskBar()
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public boolean getUsePopupMenus()
    {
        return usePopupMenus;
    }


    @Override
    public boolean getUseTabNameForTasksBand()
    {
        return useTabNameForTasksBand;
    }


    protected void setLafClassDefaults(Object[] lafClassDefaults)
    {
        this.lafClassDefaults = lafClassDefaults;
    }


    protected void setPreferredBandSize(Dimension preferredBandSize)
    {
        this.preferredBandSize = preferredBandSize;
    }


    protected void setAlwaysDisplayButtonText(boolean alwaysDisplayButtonText)
    {
        this.alwaysDisplayButtonText = alwaysDisplayButtonText;
    }


    protected void setShouldDisplayTaskBar(boolean shouldDisplayTaskBar)
    {
        this.shouldDisplayTaskBar = shouldDisplayTaskBar;
    }


    protected void setUsePopupMenus(boolean usePopupMenus)
    {
        this.usePopupMenus = usePopupMenus;
    }


    protected void setUseTabNameForTasksBand(boolean useTabNameForTasksBand)
    {
        this.useTabNameForTasksBand = useTabNameForTasksBand;
    }

}
