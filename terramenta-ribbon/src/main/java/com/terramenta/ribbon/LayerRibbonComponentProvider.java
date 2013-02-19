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
 *  o Neither the name of Chris Böhme, Pinkmatter Solutions, nor the names of
 *    any contributors may be used to endorse or promote products derived
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
package com.terramenta.ribbon;

import com.terramenta.ribbon.spi.RibbonAppMenuProvider;
import com.terramenta.ribbon.spi.RibbonComponentProvider;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenu;

/**
 * Provider for the Components on the Ribbon. The AppMenu, TaskBarand taskPanes
 *
 * @author Chris
 */
public class LayerRibbonComponentProvider extends RibbonComponentProvider {

    @Override
    public JComponent createRibbon() {
        JRibbon ribbon = new JRibbon();
        addAppMenu(ribbon);
        addTaskBar(ribbon);
        addTaskPanes(ribbon);
        return ribbon;
    }

    /**
     * For the application menu
     *
     * @param ribbon
     */
    private static void addAppMenu(JRibbon ribbon) {
        RibbonAppMenuProvider appMenuProvider = RibbonAppMenuProvider.getDefault();
        RibbonApplicationMenu appMenu = appMenuProvider.createApplicationMenu();
        if (appMenu != null) {
            ribbon.setApplicationMenu(appMenu);
        }
    }

    /**
     * For the taskBar on the right side of the menu button. Scans the layer.xml for entries in the Toolbars folder.
     *
     * @param ribbon the ribbon to add the TaskBar ActionItems too.
     */
    private static void addTaskBar(JRibbon ribbon) {
        RibbonComponentFactory factory = new RibbonComponentFactory();

        for (ActionItem item : ActionItems.forPath("Ribbon/TaskBar")) {
            if (item == null) {
                continue;
            }

            for (ActionItem child : item.getChildren()) {
                if (child == null) {
                    continue;
                }

                if (child.isSeparator()) {
                    ribbon.addTaskbarComponent(new JSeparator(JSeparator.VERTICAL));
                } else {
                    ribbon.addTaskbarComponent(factory.createTaskBarPresenter(child));
                }
            }
        }
    }

    /**
     * For the actual tabbed menu items. Scans the layer.xml for entries in the Menu folder
     *
     * @param ribbon the JRibbon to add the tabbed menu items to
     */
    private void addTaskPanes(JRibbon ribbon) {
        RibbonComponentFactory factory = new RibbonComponentFactory();
        List<ActionItem> items = new ArrayList<ActionItem>();
        items.addAll(ActionItems.forPath("Ribbon/TaskPanes"));
        items.addAll(ActionItems.forPath("Menu"));
        for (ActionItem item : items) {
            if (item == null) {
                continue;
            }
            ribbon.addTask(factory.createRibbonTask(item));
        }
    }
}
