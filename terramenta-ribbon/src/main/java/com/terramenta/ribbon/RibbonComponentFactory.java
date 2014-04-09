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
import com.terramenta.ribbon.api.RibbonPreferences;
import com.terramenta.ribbon.api.RibbonPresenter;
import com.terramenta.ribbon.spi.RibbonPreferencesProvider;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JComponent;
import org.openide.util.NbBundle;
import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.common.popup.JCommandPopupMenu;
import org.pushingpixels.flamingo.api.common.popup.JPopupPanel;
import org.pushingpixels.flamingo.api.common.popup.PopupPanelCallback;
import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

/**
 * Factory for creating Ribbon components from a NetBeans layer XML.
 *
 * See getButtonKind()
 *
 * @author Chris Böhme
 * @author Bruce Schubert (contributor)
 */
public class RibbonComponentFactory {

    private static final Logger logger = Logger.getLogger(RibbonComponentFactory.class.getName());
    // Ribbon preferences
    private static final RibbonPreferences preferences = RibbonPreferencesProvider.getDefault().getPreferences();
    // Default ribbon band size used if not provided in preferences
    private static final Dimension BAND_PREFERRED_SIZE_DEFAULT = new Dimension(40, 60);
    // Maximun number of rows in a JCommandButtonPanel before a scroll bar is activated
    private static final int BUTTON_PANEL_MAX_ROWS = 8;
    // Maximun number of columns in a JCommandButtonPanel 
    private static final int BUTTON_PANEL_MAX_COLS = 6;

    /**
     * Creates an entry for the application menu (e.g., the FILE menu).
     *
     * @param item
     * @return
     */
    RibbonApplicationMenuEntryPrimary createAppMenuPresenter(ActionItem item) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.AppMenu.class.isAssignableFrom(action.getClass())) {
            return ((RibbonPresenter.AppMenu) action).getPrimaryMenuEntry();
        } else {
            // BDS - get button kind from layer
            PrimaryMenuItem menuItem
                    = new PrimaryMenuItem(item.getIcon(),
                            item.getText(), action, getButtonKind(item));
            ArrayList<RibbonApplicationMenuEntrySecondary> secondaries = new ArrayList<>();
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

    /**
     * Creates a child menu items for the application menu.
     *
     * @param item
     * @return
     */
    RibbonApplicationMenuEntrySecondary createAppMenuSecondaryPresenter(ActionItem item) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.AppMenuSecondary.class.isAssignableFrom(action.getClass())) {
            return ((RibbonPresenter.AppMenuSecondary) action).getSecondaryMenuEntry();
        } else {
            // BDS - get button kind from layer
            SecondaryMenuItem menuItem
                    = new SecondaryMenuItem(item.getIcon(),
                            item.getText(), action, getButtonKind(item));
            menuItem.setDescriptionText(item.getDescription());
            return menuItem;
        }
    }

    private RibbonApplicationMenuEntrySecondary[] createSecondaryItems(List<ActionItem> children) {
        ArrayList<RibbonApplicationMenuEntrySecondary> secondaries = new ArrayList<>();
        for (ActionItem child : children) {
            if (!child.isSeparator()) {
                secondaries.add(createAppMenuSecondaryPresenter(child));
            }
        }
        return secondaries.toArray(new RibbonApplicationMenuEntrySecondary[secondaries.size()]);
    }

    /**
     * Creates buttons for the application footer menu based on the properties of the actionItem.
     * Creates the button with the icon and text property
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
        return createButtonPresenter(item);
    }

    public AbstractCommandButton createButtonPresenter(ActionItem item) {
        return createButtonPresenter(item, preferences.getAlwaysDisplayButtonText());
    }

    public AbstractCommandButton createButtonPresenter(ActionItem item, boolean showText) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.Button.class.isAssignableFrom(action.getClass())) {
            return ((RibbonPresenter.Button) action).getRibbonButtonPresenter();
        } else {
            String value = (String) item.getValue(ActionItem.BUTTON_STYLE);
            if (value != null && value.equalsIgnoreCase("toggle")) {
                return createCommandToggleButton(item, showText);
            } else {
                return createCommandButton(item, showText);
            }
        }
    }

    /**
     * Creates the command buttons for the ribbon and sets the rich tool tip
     *
     * @param actionItem the action item for which the CommandButton must be created
     * @return The created AbstractCommandButton
     */
    private AbstractCommandButton createCommandButton(ActionItem actionItem, boolean showText) {
        //TODO
        //button.setDisabledIcon(disabledIcon);
        ActionCommandButton button;
        String text;
        if (showText) {
            text = actionItem.getActionDelegate().getText();
        } else {
            //Determine if the icon is set. If so only display icon, otherwise display text
            ResizableIcon icon = actionItem.getActionDelegate().getIcon();
            text = (icon == null || icon.equals(ResizableIcons.EMPTY)) ? actionItem.getActionDelegate().getText() : "";
        }

        switch (getButtonKind(actionItem)) {
            case ACTION_AND_POPUP_MAIN_ACTION:
            case ACTION_AND_POPUP_MAIN_POPUP:
                // Compound actions
                button = new ActionCommandButton(
                        actionItem.getActionDelegate().getIcon(), // use the defaultAction's icon 
                        actionItem.getText(), // Use the folder's menuText for the Popup area
                        actionItem.getActionDelegate().getAction(), // the defaultAction
                        getButtonKind(actionItem));
                break;
            default:
                button = new ActionCommandButton(
                        actionItem.getActionDelegate().getIcon(),
                        text,
                        actionItem.getActionDelegate().getAction(),
                        getButtonKind(actionItem));
        }
        RichTooltip toolTip = actionItem.getActionDelegate().createTooltip();
        button.setActionRichTooltip(toolTip);
        if (actionItem.hasChildren()) {
            // Use the folder's tooltip for the popup area
            button.setPopupRichTooltip(actionItem.createTooltip());

            //final JCommandPopupMenu menu = createPopupMenu(item.getChildren());
            final JCommandPopupMenu menu = createPopupMenu(actionItem);
            button.setPopupCallback(new PopupPanelCallback() {
                @Override
                public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                    return menu;
                }
            });
        }
        Boolean isAutoRepeat = (Boolean) actionItem.getValue(ActionItem.AUTO_REPEAT_ACTION);
        if (isAutoRepeat != null) {
            button.setAutoRepeatAction(isAutoRepeat);
        }

        return button;
    }

    private AbstractCommandButton createCommandToggleButton(ActionItem actionItem, boolean showText) {
        //TODO
        //button.setDisabledIcon(disabledIcon);
        String text;
        if (showText) {
            text = actionItem.getActionDelegate().getText();
        } else {
            //Determine if the icon is set. If so only display icon, otherwise display text
            ResizableIcon icon = actionItem.getActionDelegate().getIcon();
            text = (icon == null || icon.equals(ResizableIcons.EMPTY)) ? actionItem.getActionDelegate().getText() : "";
        }
        ActionCommandToggleButton button = new ActionCommandToggleButton(
                actionItem.getActionDelegate().getIcon(),
                text,
                actionItem.getActionDelegate().getAction());

        RichTooltip toolTip = actionItem.getActionDelegate().createTooltip();
        button.setActionRichTooltip(toolTip);
        return button;
    }

    /**
     * Creates a ribbon tab (aka TaskPane) based on an actionItem (e.g., "Tools" for Menu/Tools)
     *
     * @param actionItem the ActionItem to base the RibbonTask on.
     * @return a new RibbonTask
     */
    public RibbonTask createRibbonTask(ActionItem actionItem) {
        boolean usingPopupMenus = preferences.getUsePopupMenus();
        List<AbstractRibbonBand> bands = usingPopupMenus
                ? createRibbonBandsWithPopups(actionItem)
                : createRibbonBands(actionItem);
        return new RibbonTask(actionItem.getText(), bands.toArray(new AbstractRibbonBand[bands.size()]));
    }

    /**
     * Create ribbon bands for each child folder under the root folder; creates popup menus for
     * grandchildren folders; also creates a band named "Tasks" for individual actions in the root
     * folder.
     *
     * @param actionItem representing task pane (e.g., Tools)
     * @return a collection of ribbon bands for task pane
     */
    public List<AbstractRibbonBand> createRibbonBandsWithPopups(ActionItem actionItem) {
        // E.g., for an actionItem containing:
        //     TaskPane1 (root folder) 
        //     --- Action1 (file) 
        //     --- Action2 (file) 
        //     --- Band1 (child folder)
        //     ------ Action3 (file) 
        //     --- Band2 (child folder) 
        //     ------ Popup1 (grandchild folder) 
        //     --------- Action4 (file) 
        //     --------- Action5 (file) 
        //     ------ Action6 (file)
        //
        // Creates these bands: 
        //     | Action3 | Popup1 : Action6 | Action1 : Action2 |
        //     |  Band1  |      Band2       |       Tasks       |
        //
        List<AbstractRibbonBand> bands = new ArrayList<>();
        List<ActionItem> tasks = new ArrayList<>();
        List<ActionItem> children = actionItem.getChildren();

        for (ActionItem item : children) {
            if (item.isFolder()) {
                JComponent component = item.getComponent();
                if (component instanceof AbstractRibbonBand) {
                    bands.add((AbstractRibbonBand) component);
                } else {
                    if (!item.getChildren().isEmpty()) {
                        bands.add(createRibbonBand(item));
                    }
                }
            } else {
                // Collect individual actions and separators for the "Tasks" band
                tasks.add(item);
            }
        }
        if (!tasks.isEmpty()) {
            String title = NbBundle.getMessage(RibbonComponentFactory.class, "LBL_TasksBandTitle"); // NOI18N
            if (title.isEmpty() || preferences.getUseTabNameForTasksBand()) {
                title = actionItem.getText();  // use the task pane tab name as the band name
            }
            bands.add(createRibbonBand(title, tasks));
        }
        return bands;
    }

    /**
     * Creates AbstractRibbonBands based on the ActionItem. If the ActionItem has children the
     * children are added to the ribbonBand. If the ActionItem has not only children but
     * grandChildren also, A ribbonBand is created for each child containing the grandChildren.
     *
     * @param actionItem the ActionItem to create RibbonBands for
     * @return a list of the Created AbstractRibbonBand's
     */
    public List<AbstractRibbonBand> createRibbonBands(ActionItem actionItem) {
        // E.g., for an actionItem containing:
        //     TaskPane1 (root folder) 
        //     --- Action1 (file) 
        //     --- Action2 (file) 
        //     --- Band1 (child folder)
        //     ------ Action3 (file) 
        //     --- Band2 (child folder 
        //     ------ Popup1 (grandchild folder) 
        //     --------- Action4 (file) 
        //     --------- Action5 (file) 
        //     ------ Action6 (file)
        //
        // Creates bands for the child popup folders, e.g.: 
        //     | Action3 | Action6 | Action4 : Action5 | Action1 : Action2 |
        //     |  Band1  |  Band2  |       Popup1      |      TaskPane1    |

        List<AbstractRibbonBand> bands = new ArrayList<>();
        for (ActionItem item : actionItem.getChildren()) {
            // This recursion flattens a hierarchy of popup menus--creating a band for every folder
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

    /**
     * Creates a RibbonBand for a task pane (tab).
     *
     * @param item folder
     * @return a JRibbonBand
     */
    public AbstractRibbonBand createRibbonBand(ActionItem item) {
        JRibbonBand band = createRibbonBand(item.getText(), item.getChildren());
        // BDS: create resize policies from layer.xml entries
        createResizePolicies(item, band);
        return band;
    }

    /**
     * Creates a named RibbonBand from a collection of action items.
     *
     * @param name        for the ribbon band
     * @param actionItems populate the ribbon band
     * @return a JRibbonBand
     */
    private JRibbonBand createRibbonBand(String name, List<ActionItem> actionItems) {
        //TODO icon
        boolean usingPopupMenus = preferences.getUsePopupMenus();
        JRibbonBand band = new JRibbonBand(name, ResizableIcons.empty(), getDefaultAction(actionItems));
        Dimension preferredBandSize = preferences.getPreferredBandSize();
        band.setPreferredSize(preferredBandSize != null ? preferredBandSize : BAND_PREFERRED_SIZE_DEFAULT);
        for (ActionItem child : actionItems) {
            if (child.isSeparator()) {
                band.startGroup();
            } else if (child.getValue(ActionItem.DEFAULT_ACTION) != Boolean.TRUE) {
                if (!usingPopupMenus && CommandButtonKind.POPUP_ONLY == getButtonKind(child)) {
                    continue;
                }
                addRibbonBandAction(band, child);
            }
        }
        // BDS: We're supposed to suppress of title for bands with a single item.
        if (actionItems.size() < 2 && !preferences.getAlwaysDisplayGroupText()) {
            band.setTitle("");
        }
        // Set default resize policies
        createResizePolicies(null, band);

        return band;
    }

    /**
     * @deprecated Prefer createPopuMenu(ActionItem) instead.
     * @param items
     * @return
     */
    public JCommandPopupMenu createPopupMenu(List<ActionItem> items) {
        JCommandPopupMenu menu = new JCommandPopupMenu();
        for (ActionItem item : items) {
            if (item.isSeparator()) {
                menu.addMenuSeparator();
            } else {
                menu.addMenuButton(createPopupMenuPresenter(item));
            }
        }
        return menu;
    }

    /**
     * Create a popup menu containing the children of the supplied item.
     *
     * @param item with children
     * @return a popup menu representing the child items
     */
    public JCommandPopupMenu createPopupMenu(ActionItem item) {
        if (!item.hasChildren()) {
            throw new IllegalArgumentException("ActionItem " + item.getText() + " does not have any children.");
        }
        // If the first child had children, then create a ButtonPanel as the first item in the menu
        JCommandButtonPanel panel = null;
        int i = 0;
        int maxButtonsInGroup = 0;
        while (i < item.getChildren().size()) {
            ActionItem child = item.getChildren().get(i);
            if (child.hasChildren()) {
                if (panel == null) {
                    panel = new JCommandButtonPanel(CommandButtonDisplayState.BIG);
                }
                panel.addButtonGroup(child.getText()); // TODO: getAction().getValue()
                int numButtons = 0;
                for (ActionItem grandchild : child.getChildren()) {
                    panel.addButtonToLastGroup(createButtonPresenter(grandchild, true)); // true = showText
                    ++numButtons;
                }
                maxButtonsInGroup = Math.max(numButtons, maxButtonsInGroup);
                ++i;
            } else {
                break;
            }
        }
        JCommandPopupMenu menu = (panel == null
                ? new JCommandPopupMenu()
                : new JCommandPopupMenu(panel, BUTTON_PANEL_MAX_COLS, BUTTON_PANEL_MAX_ROWS));
        while (i < item.getChildren().size()) {
            ActionItem child = item.getChildren().get(i++);
            if (child.isSeparator()) {
                menu.addMenuSeparator();
            } else {
                menu.addMenuButton(createPopupMenuPresenter(child));
            }
        }
        return menu;
    }

    /**
     * Creates a button panel for use in a popup menu. The panel contains all the children of the
     * supplied action item.
     *
     * @param actionItem
     * @return button panel hosting the child action items
     */
    public JCommandButtonPanel createButtonPanel(ActionItem actionItem) {
        JCommandButtonPanel panel = new JCommandButtonPanel(CommandButtonDisplayState.TILE);
        panel.addButtonGroup(actionItem.getText());
        for (ActionItem child : actionItem.getChildren()) {
            panel.addButtonToLastGroup(createButtonPresenter(child, true)); // true = show text
        }
        return panel;
    }

    public JCommandMenuButton createPopupMenuPresenter(ActionItem item) {
        //TODO orientation of popup
        ActionMenuButton button = new ActionMenuButton(item.getIcon(),
                item.getText(), item.getAction(), getButtonKind(item));
        RichTooltip toolTip = item.createTooltip();
        button.setActionRichTooltip(toolTip);
        if (item.hasChildren()) {
            //TODO differentiate between the two
            //button.setPopupRichTooltip(tooltip);
            final JCommandPopupMenu menu = createPopupMenu(item.getChildren());
            button.setPopupCallback(new PopupPanelCallback() {
                @Override
                public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                    return menu;
                }
            });
        }
        return button;
    }

    private void createResizePolicies(ActionItem actionItem, JRibbonBand band) {
        // BDS: Allow the bands to resize themselves to the window frame width
        ArrayList<RibbonBandResizePolicy> resizePolicies = new ArrayList<>();
        if (actionItem != null) {
            String listOfPolicies = (String) actionItem.getValue("resizePolicies");
            if (listOfPolicies != null) {
                StringTokenizer st = new StringTokenizer(listOfPolicies);
                while (st.hasMoreTokens()) {
                    String token = st.nextToken(" ,");
                    if (token.equalsIgnoreCase("Mirror")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.Mirror(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("High2Mid")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.High2Mid(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("High2Low")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.High2Low(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("Mid2Mid")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.Mid2Mid(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("Mid2Low")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.Mid2Low(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("Low2Mid")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.Low2Mid(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("None")) {
                        resizePolicies.add(new CoreRibbonResizePolicies.None(band.getControlPanel()));
                    } else if (token.equalsIgnoreCase("Iconified")) {
                        // This policy can only appear once and it must be the last policy
                        resizePolicies.add(new IconRibbonBandResizePolicy(band.getControlPanel()));
                        break;  // exit the loop to enforce this policy's rule for placement and multiplicity
                    } else {
                        logger.log(Level.WARNING, "The token ''{0}'' is not recognized as a "
                                + "valid resize policy for ''{1}'' -- it will be ignored.",
                                new Object[]{
                                    token, actionItem.getText()
                                });
                    }
                }
            }
        }
        if (resizePolicies.isEmpty()) {
            // The policies must be ordered from widest to narrowist.
//        Insets ins = band.getInsets();
//        JBandControlPanel controlPanel = band.getControlPanel();
//        int height = controlPanel.getPreferredSize().height
//                + band.getUI().getBandTitleHeight() + ins.top
//                + ins.bottom;
//
//        ArrayList<RibbonBandResizePolicy> resizePolicies = new ArrayList<RibbonBandResizePolicy>();
//        List<RibbonBandResizePolicy> candidatePolicies = CoreRibbonResizePolicies.getCorePoliciesRestrictive(band);
//        for (int i = 0; i < (candidatePolicies.size() - 1); i++)
//        {
//            RibbonBandResizePolicy policy1 = candidatePolicies.get(i);
//            RibbonBandResizePolicy policy2 = candidatePolicies.get(i + 1);
//            int width1 = policy1.getPreferredWidth(height, 4);
//            int width2 = policy2.getPreferredWidth(height, 4);
//            if (i == 0)
//            {
//                resizePolicies.add(policy1);
//            }
//            else if (width1 > width2)
//            {
//                resizePolicies.add(policy2);
//            }
//        }
//        band.setResizePolicies(resizePolicies);
//        FlamingoUtilities.checkResizePoliciesConsistency(band); -- THIS FAILS! Workaround below

            // BDS: Workaround 
            // Subsequent calls to FlamingoUtilities.checkResizePoliciesConsistency in layoutContainer fail!
            // So, we'll manually create a simple list of resize policies until we figure out what's
            // altering the computed sizes for High2Mid.        
            logger.log(Level.CONFIG, "Using the default resize policies for ''{0}''.", actionItem == null ? band.getTitle() : actionItem.getText());
            resizePolicies.add(new CoreRibbonResizePolicies.Mirror(band.getControlPanel()));
            resizePolicies.add(new CoreRibbonResizePolicies.Mid2Low(band.getControlPanel()));
            resizePolicies.add(new CoreRibbonResizePolicies.High2Low(band.getControlPanel()));
        }
        band.setResizePolicies(resizePolicies);
    }

    private static ActionListener getDefaultAction(List<ActionItem> actionItems) {
        for (ActionItem child : actionItems) {
            if (child.getValue(ActionItem.DEFAULT_ACTION) == Boolean.TRUE
                    && child.getAction() != null) {
                return child.getAction();
            }
        }
        return null;
    }

    private void addRibbonBandAction(JRibbonBand band, ActionItem item) {
        Action action = item.getAction();
        if (action != null && RibbonPresenter.Component.class.isAssignableFrom(action.getClass())) {
            //TODO calculate height
            band.addRibbonComponent(((RibbonPresenter.Component) action).getRibbonBarComponentPresenter(), 3);
        } else {
            band.addCommandButton(createButtonPresenter(item), getPriority(item));
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

    /**
     * This class associates an Action with the JCommandButton.
     */
    private static class ActionCommandButton extends JCommandButton {

        ActionCommandButton(ResizableIcon icon, String text, final Action action,
                CommandButtonKind type) {
            super(text, icon);
            setCommandButtonKind(type);
            if (action != null) {
                addActionListener(action);
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name and ShortDescription properties.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        switch (evt.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) evt.getNewValue());
                                break;
                            case Action.SHORT_DESCRIPTION:
                                setToolTipText((String) evt.getNewValue());
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    /**
     * This class associates an Action with the JCommandToggleMenuButton.
     */
    private static class ActionCommandToggleButton extends JCommandToggleButton {

        private static final Logger logger = Logger.getLogger(ActionCommandToggleButton.class.getName());

        ActionCommandToggleButton(ResizableIcon icon, String text, final Action action) {

            super(text, icon);
            if (action != null) {
                addActionListener(action);
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name property.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent event) {
                        switch (event.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) event.getNewValue());
                                break;
                            case Action.SELECTED_KEY:
                                Object newValue = event.getNewValue();
                                getActionModel().setSelected(newValue != null ? (Boolean) newValue : false);
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());

                /*
                 * BDS: 20110914 added establishment of the Selected state based on the action's property
                 */
                Object currentValue = action.getValue(Action.SELECTED_KEY);
                getActionModel().setSelected(currentValue != null ? (Boolean) currentValue : false);
            }
        }
    }

    /**
     * This class associates an Action with the JCommandMenuButton.
     */
    private static class ActionMenuButton extends JCommandMenuButton {

        ActionMenuButton(ResizableIcon icon, String text, final Action action,
                CommandButtonKind type) {
            super(text, icon);
            setCommandButtonKind(type);
            if (action != null) {
                addActionListener(action);

                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name property.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        switch (evt.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) evt.getNewValue());
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    /**
     * This class associates an Action with the JCommandToggleMenuButton.
     */
    private static class ActionToggleMenuButton extends JCommandToggleMenuButton {

        ActionToggleMenuButton(ResizableIcon icon, String text, final Action action) {
            super(text, icon);
            if (action != null) {
                addActionListener(action);

                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name property.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        switch (evt.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) evt.getNewValue());
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    /**
     * This class associates an Action PropertyChangeListener with the Primary Menu Entry.
     */
    private static class PrimaryMenuItem extends RibbonApplicationMenuEntryPrimary {

        PrimaryMenuItem(ResizableIcon icon, String text, final Action action,
                CommandButtonKind type) {
            super(icon, text, action, type);

            if (action != null) {
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name property.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        switch (evt.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) evt.getNewValue());
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    /**
     * This class associates an Action PropertyChangeListener with the Secondary Menu Entry.
     */
    private static class SecondaryMenuItem extends RibbonApplicationMenuEntrySecondary {

        SecondaryMenuItem(ResizableIcon icon, String text, final Action action, CommandButtonKind type) {
            super(icon, text, action, type);

            if (action != null) {
                action.addPropertyChangeListener(new PropertyChangeListener() {
                    /**
                     * BDS: 20110509 added handling of the Name property.
                     */
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        switch (evt.getPropertyName()) {
                            case "enabled":
                                setEnabled(action.isEnabled());
                                break;
                            case Action.NAME:
                                setText((String) evt.getNewValue());
                                break;
                        }
                    }
                });
                setEnabled(action.isEnabled());
            }
        }
    }

    /**
     * Get the command button kind based on the context of an action delegate and children.
     *
     * @param item to evaluate
     * @return POPUP_ONLY, ACTION_AND_POPUP_MAIN_ACTION or ACTION_ONLY
     */
    private static CommandButtonKind getButtonKind(ActionItem item) {
        Action delegate = item.getActionDelegate().getAction();
        if (delegate == null && item.hasChildren()) {
            return CommandButtonKind.POPUP_ONLY;
        } else if (delegate != null && item.hasChildren()) {
            return CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION;
        } else if (delegate != null && !item.hasChildren()) {
            return CommandButtonKind.ACTION_ONLY;
        } else {
            return CommandButtonKind.POPUP_ONLY;
        }
    }

}
