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

import com.terramenta.ribbon.api.ResizableIcons;
import com.terramenta.ribbon.api.RibbonPresenter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComponent;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.AbstractRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryFooter;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntryPrimary;
import org.pushingpixels.flamingo.api.ribbon.RibbonApplicationMenuEntrySecondary;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies.Mid2Mid;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 * Factory for creating ribbonComponents. Will create toolBar and ribbon components
 *
 * @author Chris
 */
class RibbonComponentFactory {

    public RibbonApplicationMenuEntryPrimary createAppMenuPresenter(ActionItem item) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.AppMenu.class.isAssignableFrom(action.getClass())) {
            return ((RibbonPresenter.AppMenu) action).getPrimaryMenuEntry();
        } else {
            PrimaryMenuItem menuItem =
                    new PrimaryMenuItem(item.getIcon(),
                    item.getText(), action, CommandButtonKind.ACTION_ONLY);
            ArrayList<RibbonApplicationMenuEntrySecondary> secondaries = new ArrayList<RibbonApplicationMenuEntrySecondary>();
            for (ActionItem child : item.getChildren()) {
                if (child.getAction() == null) {
                    menuItem.addSecondaryMenuGroup(child.getText(), createSecondaryItems(child.getChildren()));
                } else {
                    if (!child.isSeparator()) {
                        secondaries.add(createAppMenuSecondaryPresenter(child));
                    }
                }
            }

            RibbonApplicationMenuEntrySecondary[] secondary = secondaries.toArray(new RibbonApplicationMenuEntrySecondary[secondaries.size()]);
            if (secondary != null && secondary.length > 0) {
                menuItem.addSecondaryMenuGroup(null, secondary);
            }
            return menuItem;
        }
    }

    private RibbonApplicationMenuEntrySecondary createAppMenuSecondaryPresenter(ActionItem item) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.AppMenuSecondary.class.isAssignableFrom(action.getClass())) {
            return ((RibbonPresenter.AppMenuSecondary) action).getSecondaryMenuEntry();
        } else {
            SecondaryMenuItem menuItem =
                    new SecondaryMenuItem(item.getIcon(),
                    item.getText(), action, CommandButtonKind.ACTION_ONLY);
            menuItem.setDescriptionText(item.getDescription());
            return menuItem;
        }
    }

    private RibbonApplicationMenuEntrySecondary[] createSecondaryItems(List<ActionItem> children) {
        ArrayList<RibbonApplicationMenuEntrySecondary> secondaries = new ArrayList<RibbonApplicationMenuEntrySecondary>();
        for (ActionItem child : children) {
            if (!child.isSeparator()) {
                secondaries.add(createAppMenuSecondaryPresenter(child));
            }
        }
        return secondaries.toArray(new RibbonApplicationMenuEntrySecondary[secondaries.size()]);
    }

    /**
     * Creates buttons for the application footer menu based on the properties of the actionItem. Creates the button
     * with the icon and text property
     *
     * @param item the item to base the button on
     * @return RibbonApplicationMenuEntryFooter created.
     */
    public RibbonApplicationMenuEntryFooter createAppMenuFooterPresenter(ActionItem item) {
        RibbonApplicationMenuEntryFooter footer = new RibbonApplicationMenuEntryFooter(
                item.getIcon(), item.getText(), item.getAction());
        return footer;
    }

    /**
     * Create a taskBar button based on the actionItem
     *
     * @param item the item to base the button on
     * @return Component the button created
     */
    public Component createTaskBarPresenter(ActionItem item) {
        return createCommandButton(item);
    }

    /**
     * Creates the command buttons for the ribbon and sets the rich tool tip
     *
     * @param item the action item for which the CommandButton must be created
     * @return The created AbstractCommandButton
     */
    private AbstractCommandButton createCommandButton(ActionItem item) {
        //Determine if the icon is set. If so only display icon otherwise display text
        String text = (item.getActionDelegate().getIcon() == null ? item.getActionDelegate().getText() : "");
        ActionCommandButton button = new ActionCommandButton(item.getActionDelegate().getIcon(), text, item.getActionDelegate().getAction(), CommandButtonKind.ACTION_ONLY);
        RichTooltip toolTip = item.getActionDelegate().createTooltip();
        button.setActionRichTooltip(toolTip);
        return button;
    }

    /**
     * Creates a Ribbon task based on an actionItem
     *
     * @param actionItem the ActionItem to base the RibbonTask on.
     * @return the Created RibbonTask
     */
    public RibbonTask createRibbonTask(ActionItem actionItem) {
        List<AbstractRibbonBand> bands = createRibbonBands(actionItem);
        return new RibbonTask(actionItem.getText(), bands.toArray(new AbstractRibbonBand[bands.size()]));
    }

    /**
     * Creates AbstractRibbonBans based on the ActionItem. If the ActionItem has children the children are added to the
     * ribbonBand. if the ActionItem has not only children but grandChildren also, A ribbonBand is created for each
     * child containing the grandChildren
     *
     * @param actionItem the ActionItem to create RibbonBands for
     * @return a list of the Created AbstractRibbonBand's
     */
    public List<AbstractRibbonBand> createRibbonBands(ActionItem actionItem) {
        List<AbstractRibbonBand> bands = new ArrayList<AbstractRibbonBand>();

        for (ActionItem item : actionItem.getChildren()) {
            bands.addAll(createRibbonBands(item));
        }

        JComponent component = actionItem.getComponent();
        if (component != null && AbstractRibbonBand.class.isAssignableFrom(component.getClass())) {
            bands.add((AbstractRibbonBand) component);
        } else {
            if (actionItem.getChildren().size() > 0) {
                bands.add(createRibbonBand(actionItem));
            }
        }

        return bands;
    }

    public AbstractRibbonBand createRibbonBand(ActionItem item) {
        JRibbonBand band = new JRibbonBand(item.getText(), ResizableIcons.empty(), getDefaultAction(item));
        for (ActionItem child : item.getChildren()) {
            if (child.getChildren().isEmpty()) {
                if (child.isSeparator()) {
                    band.startGroup();
                } else if (child.getValue(ActionItem.DEFAULT_ACTION) != Boolean.TRUE) {
                    addRibbonBandAction(band, child);
                }
            }
        }
        band.setPreferredSize(new Dimension(40, 60));
        band.setResizePolicies(Arrays.<RibbonBandResizePolicy>asList(new Mid2Mid(band.getControlPanel())));
        return band;
    }

    private static ActionListener getDefaultAction(ActionItem item) {
        for (ActionItem child : item.getChildren()) {
            if (child.getValue(ActionItem.DEFAULT_ACTION) == Boolean.TRUE
                    && child.getAction() != null) {
                return child.getAction();
            }
        }
        return null;
    }

    private void addRibbonBandAction(JRibbonBand band, ActionItem item) {
        Action action = item.getAction();
        if (action == null) {
            return;
        }

        if (RibbonPresenter.Component.class.isAssignableFrom(action.getClass())) {
            band.addRibbonComponent(((RibbonPresenter.Component) action).getRibbonBarComponentPresenter(), 3);
        } else {
            band.addCommandButton(createCommandButton(item), getPriority(item));
        }
    }

    private static RibbonElementPriority getPriority(ActionItem item) {
        RibbonElementPriority p = RibbonElementPriority.TOP;
        String priority = (String) item.getValue("priority");
        if (priority != null) {
            p = RibbonElementPriority.valueOf(priority.toUpperCase());
        }
        return p;
    }

    private static class ActionCommandButton extends JCommandButton {

        public ActionCommandButton(ResizableIcon icon, String text, final Action action,
                CommandButtonKind type) {
            super(text, icon);
            setCommandButtonKind(type);
            if (action != null) {
                addActionListener(action);
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) {
                            setEnabled(action.isEnabled());
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    private static class PrimaryMenuItem extends RibbonApplicationMenuEntryPrimary {

        public PrimaryMenuItem(ResizableIcon icon, String text, final Action action, CommandButtonKind type) {
            super(icon, text, action, type);

            if (action != null) {
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) {
                            setEnabled(action.isEnabled());
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    private static class SecondaryMenuItem extends RibbonApplicationMenuEntrySecondary {

        public SecondaryMenuItem(ResizableIcon icon, String text, final Action action, CommandButtonKind type) {
            super(icon, text, action, type);

            if (action != null) {
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("enabled".equals(evt.getPropertyName())) {
                            setEnabled(action.isEnabled());
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }
}
