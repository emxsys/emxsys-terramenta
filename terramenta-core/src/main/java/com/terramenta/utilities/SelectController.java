/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.terramenta.utilities;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.util.ImageUtilities;

/**
 *
 * @author heidtmare
 */
public class SelectController implements SelectListener, Disposable {

    private static AVList lastSelect = null;
    private static AVList lastHover = null;
    private static AVList lastRollover = null;
    private final WorldWindow wwd;
    private boolean armed = false;

    /**
     *
     * @param wwd
     */
    public SelectController(WorldWindow wwd) {
        if (wwd == null) {
            throw new IllegalArgumentException("nullValue.WorldWindow");
        }
        this.wwd = wwd;
    }

    /**
     *
     */
    @Override
    public void dispose() {
        setArmed(false);
    }

    /**
     *
     * @return
     */
    public boolean isArmed() {
        return armed;
    }

    /**
     *
     * @param armed
     */
    public void setArmed(boolean armed) {
        if (this.armed == armed) {
            return;
        }
        this.armed = armed;
        if (armed) {
            this.wwd.addSelectListener(this);
        } else {
            this.wwd.removeSelectListener(this);
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void selected(SelectEvent e) {
        if (e.isLeftClick() && e.hasObjects()) {
            doSelect(e.getTopObject());
        } else if (e.isRightClick() && e.hasObjects()) {
            PickedObjectList pickedObjects = e.getObjects();
            if (!pickedObjects.hasNonTerrainObjects()) {
                return;
            }

            JPopupMenu popup = new JPopupMenu();
            for (PickedObject po : pickedObjects) {
                if (po.isTerrain()) {
                    continue;
                }

                Object userObject = po.getObject();
                String name = userObject.getClass().getName();
                Icon icon = null;
                if (userObject instanceof AVList) {
                    AVList av = (AVList) userObject;
                    if (av.hasKey(AVKey.DISPLAY_NAME)) {
                        name = av.getStringValue(AVKey.DISPLAY_NAME);
                    }
                    if (av.hasKey(AVKey.DISPLAY_ICON)) {
                        icon = ImageUtilities.loadImageIcon(av.getStringValue(AVKey.DISPLAY_ICON), true);
                    }
                }
                popup.add(new JMenuItem(new ContextMenuItemAction(name, icon, userObject)));
            }
            popup.show((Component) e.getSource(), e.getMouseEvent().getX(), e.getMouseEvent().getY());
        } else if (e.isHover() && e.hasObjects()) {
            doHover(e.getTopObject());
        } else if (e.isRollover() && e.hasObjects()) {
            doRollover(e.getTopObject());
        }

        e.consume();
    }

    private static void doSelect(Object obj) {
        if (lastSelect != null) {
            lastSelect.firePropertyChange("SELECT", null, false);
            lastSelect = null;
        }

        if (obj instanceof AVList) {
            AVList avl = (AVList) obj;
            avl.firePropertyChange("SELECT", null, true);
            lastSelect = avl;
        }
//we use to open a properties sheet by default, but it sometimes opened at undesireble moments
//        else {
//            try {
//                NodeOperation.getDefault().showProperties(new BeanNode(obj));
//            } catch (IntrospectionException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
    }

    private static void doHover(Object obj) {
        if (lastHover != null) {
            lastHover.firePropertyChange("HOVER", null, false);
            lastHover = null;
        }

        if (obj instanceof AVList) {
            AVList avl = (AVList) obj;
            avl.firePropertyChange("HOVER", null, true);
            lastHover = avl;
        }
    }

    private static void doRollover(Object obj) {
        if (lastRollover != null) {
            lastRollover.firePropertyChange("ROLLOVER", null, false);
            lastRollover = null;
        }

        if (obj instanceof AVList) {
            AVList avl = (AVList) obj;
            avl.firePropertyChange("ROLLOVER", null, true);
            lastRollover = avl;
        }
    }

    /**
     *
     */
    public static class ContextMenuItemAction extends AbstractAction {

        private final Object sel;

        /**
         *
         * @param name
         * @param icon
         * @param sel
         */
        public ContextMenuItemAction(String name, Icon icon, Object sel) {
            super(name, icon);
            this.sel = sel;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            doSelect(sel);
        }
    }
}
