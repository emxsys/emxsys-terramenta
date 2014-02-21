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
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import org.openide.util.ImageUtilities;
import org.pushingpixels.flamingo.api.common.RichTooltip;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

/**
 * The elements of an Action that can be represented in a Ribbon button.
 * 
 * @author Chris
 */
public abstract class ActionItem {

    public static final String MENU_TEXT = "menuText";
    public static final String POPUP_TEXT = "popupText";
    public static final String DESCRIPTION = "description";
    public static final String DISPLAY_NAME = "displayName";
    public static final String ICON_BASE = "iconBase";
    public static final String TOOLTIP_BODY = "tooltipBody";
    public static final String TOOLTIP_TITLE = "tooltipTitle";
    public static final String TOOLTIP_ICON = "tooltipIcon";
    public static final String TOOLTIP_FOOTER = "tooltipFooter";
    public static final String TOOLTIP_FOOTER_ICON = "tooltipFooterIcon";
    public static final String DEFAULT_ACTION = "defaultAction";
    public static final String BUTTON_STYLE = "buttonStyle";
    public static final String AUTO_REPEAT_ACTION = "autoRepeatAction";

    public static ActionItem separator() {
        return new Separator();
    }

    public static ActionItem actions(Action action) {
        return new Actions(action);
    }

    public static ActionItem component(JComponent component) {
        return new Component(component);
    }
    private Map<String, Object> properties;

    public Action getAction() {
        return null;
    }

    public List<ActionItem> getChildren() {
        return Collections.emptyList();
    }

    public void addChild(ActionItem item) {
    }

    public boolean hasChildren() {
        return false;
    }

    public JComponent getComponent() {
        return null;
    }

    public ActionItem getActionDelegate() {
        return this;
    }

    public Object getValue(String key) {
        Object value = innerGetValue(key);
        if (value == null) {
            Action action = getAction();
            if (action != null) {
                value = action.getValue(key);
            }
        }
        if (value == null) {
            JComponent component = getComponent();
            if (component != null) {
                value = component.getClientProperty(value);
            }
        }
        return value;
    }

    public void putValue(String key, Object value) {
        if (properties == null) {
            properties = new TreeMap<String, Object>();
        }
        properties.put(key, value);
    }

    private Object innerGetValue(String key) {
        if (properties == null) {
            return null;
        } else {
            return properties.get(key);
        }
    }

    /**
     *
     * @return the displayName
     */
    public String getName() {
        String s = String.valueOf(getValue(Action.NAME));
        return s != null ? org.openide.awt.Actions.cutAmpersand(s) : null;
    }

    /**
     *
     * @return the menuText if defined, otherwise the displayName
     */
    public String getText() {
        String s;
        if (getValue(MENU_TEXT) != null) {
            s = getValue(MENU_TEXT).toString();
        } else if (getValue(DISPLAY_NAME) != null) {
            s = getValue(DISPLAY_NAME).toString();
        } else {
            s = String.valueOf(getValue(Action.NAME));
        }
        return s != null ? org.openide.awt.Actions.cutAmpersand(s) : null;
    }

    public void setText(String name) {
        putValue(MENU_TEXT, name);
    }

    public String getDescription() {
        String s = null;
        if (getValue(DESCRIPTION) != null) {
            s = getValue(DESCRIPTION).toString();
        } else if (getValue(Action.SHORT_DESCRIPTION) != null) {
            s = String.valueOf(getValue(Action.SHORT_DESCRIPTION));
        } else if (getValue(POPUP_TEXT) != null) {
            s = getValue(POPUP_TEXT).toString();
        }
        return s;
    }

    public RichTooltip createTooltip() {
        String body = (String) getValue(TOOLTIP_BODY);
        if (body == null) {
            body = (String) getValue(Action.LONG_DESCRIPTION);
        }
        if (body == null) {
            body = getDescription();
        }
        if (body == null) {
            return null;
        }
        // Parse the body for multi-line descriptions
        final String DELIM = "\n";
        StringTokenizer st = new StringTokenizer(body, DELIM, false);
        ArrayList<String> lines = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            lines.add(st.nextToken());
        }

        String title = (String) getValue(TOOLTIP_TITLE);
        if (title == null) {
            title = getText();
        }

        // Create the tooltip
        RichTooltip tooltip = new RichTooltip(title, lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            tooltip.addDescriptionSection(lines.get(i));
        }

        String titleIcon = (String) getValue(TOOLTIP_ICON);
        if (titleIcon != null) {
            tooltip.setMainImage(ImageUtilities.loadImage(titleIcon));
        } else {
            tooltip.setMainImage(getLargeImage());
        }

        String footer = (String) getValue(TOOLTIP_FOOTER);
        if (footer != null) {
            tooltip.addFooterSection(footer);
            String footerIcon = (String) getValue(TOOLTIP_FOOTER_ICON);
            if (footerIcon != null) {
                tooltip.setFooterImage(ImageUtilities.loadImage(footerIcon));
            }
        }
        return tooltip;
    }

    private Image getLargeImage() {
        String iconResource = (String) getValue(ICON_BASE);
        Image image = null;
        if (iconResource != null) {
            image = ImageUtilities.loadImage(Utils.insertBeforeSuffix(iconResource, 48));
            if (image == null) {
                image = ImageUtilities.loadImage(Utils.insertBeforeSuffix(iconResource, 32));
            }
            if (image == null) {
                image = ImageUtilities.loadImage(Utils.insertBeforeSuffix(iconResource, 24));
            }
        }
        if (image == null) {
            Object iconKey = getValue(Action.LARGE_ICON_KEY);
            if (iconKey instanceof Image) {
                image = (Image) iconKey;
            }
        }
        return image;
    }

    /**
     * Get the icon of the ActionItem.
     *
     * @return If not null: icon defined under ActionItem.ICON_BASE Otherwise If both not null:
     * ResizableIcons.binary(Action.SMALL_ICON, Action.LARGE_ICON_KEY); Otherwise an empty icon
     */
    public ResizableIcon getIcon() {
        String iconResource = (String) getValue(ICON_BASE);
        if (iconResource != null) {
            return ResizableIcons.fromResource(iconResource);
        } else {
            Icon small = (Icon) getValue(Action.SMALL_ICON);
            Icon large = (Icon) getValue(Action.LARGE_ICON_KEY);
            if (small != null || large != null) {
                return ResizableIcons.binary(small, large);
            } else {
                return ResizableIcons.empty();
            }
        }
    }

    public boolean isSeparator() {
        return false;
    }

    public boolean isFolder()
    {
        return !isSeparator() && getActionDelegate().getAction()==null;
    }
    
    @Override
    public String toString() {
        return toString("");
    }

    private String toString(String prefix) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(prefix);
        buffer.append(getText());
        buffer.append('\n');
        for (ActionItem child : getChildren()) {
            buffer.append(child.toString(prefix + "---"));
        }
        return buffer.toString();
    }

    public static class Actions extends ActionItem {

        private Action action;

        public Actions(Action action) {
            this.action = action;
        }

        @Override
        public Action getAction() {
            return action;
        }
    }

    private static class Separator extends ActionItem {

        Separator() {
        }

        @Override
        public boolean isSeparator() {
            return true;
        }
    }

    private static class Component extends ActionItem {

        private JComponent _component;

        Component(JComponent component) {
            _component = component;
        }

        @Override
        public JComponent getComponent() {
            return _component;
        }
    }

    public static class Compound extends ActionItem {

        private List<ActionItem> _children;

        public Compound() {
            _children = null;
        }

        @SuppressWarnings("unchecked")
        public Compound(Collection<ActionItem> children) {
            _children = new ArrayList(children);
        }

        @Override
        public ActionItem getActionDelegate() {
            for (ActionItem child : getChildren()) {
                if (child.getValue("defaultAction") == Boolean.TRUE) {
                    return child;
                }
            }
            return super.getActionDelegate();
        }

        @Override
        public List<ActionItem> getChildren() {
            if (_children == null) {
                _children = new ArrayList<>();
            }
            return _children;
        }

        @Override
        public void addChild(ActionItem item) {
            getChildren().add(item);
        }

        @Override
        public boolean hasChildren() {
            return _children != null && _children.size() > 0;
        }
    }
}
