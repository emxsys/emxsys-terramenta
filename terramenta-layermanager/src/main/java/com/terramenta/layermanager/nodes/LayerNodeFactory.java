/*
 * Copyright © 2014, Terramenta. All rights reserved.
 *
 * This work is subject to the terms of either
 * the GNU General Public License Version 3 ("GPL") or 
 * the Common Development and Distribution License("CDDL") (collectively, the "License").
 * You may not use this work except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/CDDL-1.0
 * http://opensource.org/licenses/GPL-3.0
 */
package com.terramenta.layermanager.nodes;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author heidtmare
 */
public class LayerNodeFactory extends Children.Keys<Layer> implements Index {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private Index indexSupport;

    /**
     *
     */
    public LayerNodeFactory() {
        wwm.getLayers().addPropertyChangeListener((PropertyChangeEvent evt) -> {
            if (evt.getPropertyName().equals(AVKey.LAYERS)) {
                addNotify();
            }
        });
    }

    /**
     *
     */
    @Override
    protected void addNotify() {
        LayerList ll = wwm.getLayers();
        indexSupport = new IndexSupport(ll, getNode());
        setKeys(ll);
    }

    /**
     *
     * @param layer
     * @return
     */
    @Override
    protected Node[] createNodes(Layer layer) {
        try {
            return new Node[]{new LayerNode(layer)};
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     *
     * @param l
     */
    @Override
    public void addChangeListener(ChangeListener l) {
        indexSupport.addChangeListener(l);
    }

    /**
     *
     * @param l
     */
    @Override
    public void removeChangeListener(ChangeListener l) {
        indexSupport.removeChangeListener(l);
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public void exchange(int x, int y) {
        indexSupport.exchange(x, y);
    }

    /**
     *
     * @param node
     * @return
     */
    @Override
    public int indexOf(Node node) {
        return indexSupport.indexOf(node);
    }

    /**
     *
     * @param i
     */
    @Override
    public void moveUp(int i) {
        indexSupport.moveUp(i);
    }

    /**
     *
     * @param i
     */
    @Override
    public void moveDown(int i) {
        indexSupport.moveDown(i);
    }

    /**
     *
     * @param x
     * @param y
     */
    @Override
    public void move(int x, int y) {
        indexSupport.move(x, y);
    }

    /**
     *
     */
    @Override
    public void reorder() {
        indexSupport.reorder();
    }

    /**
     *
     * @param i
     */
    @Override
    public void reorder(int[] i) {
        indexSupport.reorder(i);
    }

    private class IndexSupport extends Index.Support {

        LayerList keyArrayList;
        Node myNode;

        public IndexSupport(LayerList keyArrayList, Node myNode) {
            this.keyArrayList = keyArrayList;
            this.myNode = myNode;
        }

        @Override
        public void reorder(int[] perm) {
            LayerList clone = new LayerList();
            clone.addAll(keyArrayList);
            for (int i = 0; i < keyArrayList.size(); i++) {
                keyArrayList.set(perm[i], clone.get(i));
            }
            setKeys(keyArrayList);
        }

        @Override
        public int getNodesCount() {
            return myNode.getChildren().getNodesCount();
        }

        @Override
        public Node[] getNodes() {
            return myNode.getChildren().getNodes();
        }
    }
}
