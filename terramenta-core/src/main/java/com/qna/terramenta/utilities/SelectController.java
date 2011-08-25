/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.utilities;

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
import java.beans.IntrospectionException;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.openide.nodes.BeanNode;
import org.openide.nodes.NodeOperation;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author heidtmare
 */
public class SelectController implements SelectListener, Disposable {

    private final WorldWindow wwd;

    /**
     *
     * @param wwd
     */
    public SelectController(WorldWindow wwd) {
        this.wwd = wwd;
        this.wwd.addSelectListener(this);
    }

    /**
     * 
     */
    @Override
    public void dispose() {
        this.wwd.removeSelectListener(this);
    }

    /**
     * 
     * @param e
     */
    @Override
    public void selected(SelectEvent e) {
        if (e.isLeftClick() && e.hasObjects()) {
            /*try {
                NodeOperation.getDefault().showProperties(new BeanNode(e.getTopObject()));
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }*/
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
            try {
                NodeOperation.getDefault().showProperties(new BeanNode(sel));
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
