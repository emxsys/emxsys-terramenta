/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package com.terramenta.globe.utilities;

import gov.nasa.worldwind.Disposable;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.pick.PickedObject;
import gov.nasa.worldwind.pick.PickedObjectList;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.util.ImageUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author heidtmare
 */
public class SelectController implements SelectListener, Disposable {

    private static final Logger logger = LoggerFactory.getLogger(SelectController.class);
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
        if (!e.hasObjects()) {
            return;
        }

        if (e.isLeftPress()) {
            doSelect(e.getPickPoint(), e.getTopObject());

        } else if (e.isHover()) {
            doHover(e.getPickPoint(), e.getTopObject());

        } else if (e.isRollover()) {
            doRollover(e.getPickPoint(), e.getTopObject());

        } else if (e.isRightClick()) {
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
                popup.add(new JMenuItem(new ContextMenuItemAction(name, icon, e.getPickPoint(), userObject)));
            }
            popup.show((Component) e.getSource(), e.getMouseEvent().getX(), e.getMouseEvent().getY());

            //assuming its safe to consume after displaying a popup
            e.consume();
        }
    }

    private static void doSelect(Point point, Object obj) {

        //prevent multiple select events from a single item within a 10 millisecond window
        if (obj.equals(lastSelect)) {
            if (lastSelect.hasKey("selectionTimeMillis")) {
                if ((System.currentTimeMillis() - (long) lastSelect.getValue("selectionTimeMillis")) < 10) {
                    return;
                }
            }
        }

        if (lastSelect != null) {
            logger.info("Unselecting {}", lastSelect);
            lastSelect.firePropertyChange("SELECT", null, null);
            lastSelect = null;
        }

        if (obj instanceof AVList) {
            logger.info("Selecting {}", obj);
            AVList avl = (AVList) obj;
            avl.setValue("selectionTimeMillis", System.currentTimeMillis());
            avl.firePropertyChange("SELECT", null, point);
            lastSelect = avl;
        }
    }

    private static void doHover(Point point, Object obj) {
        if (lastHover != null) {
            logger.debug("Unhovering {}", lastHover);
            lastHover.firePropertyChange("HOVER", null, null);
            lastHover = null;
        }

        if (obj instanceof AVList) {
            logger.debug("Hovering {}", obj);
            AVList avl = (AVList) obj;
            avl.firePropertyChange("HOVER", null, point);
            lastHover = avl;
        }
    }

    private static void doRollover(Point point, Object obj) {
        if (lastRollover != null) {
            logger.debug("Unrollovering {}", lastRollover);
            lastRollover.firePropertyChange("ROLLOVER", null, null);
            lastRollover = null;
        }

        if (obj instanceof AVList) {
            logger.debug("Rollovering {}", obj);
            AVList avl = (AVList) obj;
            avl.firePropertyChange("ROLLOVER", null, point);
            lastRollover = avl;
        }
    }

    /**
     *
     */
    public static class ContextMenuItemAction extends AbstractAction {

        private final Object sel;
        private final Point pnt;

        /**
         *
         * @param name
         * @param icon
         * @param pnt
         * @param sel
         */
        public ContextMenuItemAction(String name, Icon icon, Point pnt, Object sel) {
            super(name, icon);
            this.pnt = pnt;
            this.sel = sel;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            doSelect(pnt, sel);
        }
    }
}
