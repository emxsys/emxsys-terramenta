/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.layermanager.nodes;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Chris.Heidt
 */
public class RenderableChildFactory extends ChildFactory.Detachable<Renderable> implements PropertyChangeListener {

    private final RenderableLayer layer;

    public RenderableChildFactory(RenderableLayer layer) {
        this.layer = layer;
    }

    @Override
    protected void addNotify() {
        layer.addPropertyChangeListener(this);
    }

    @Override
    protected void removeNotify() {
        layer.removePropertyChangeListener(this);
    }

    @Override
    protected boolean createKeys(List<Renderable> list) {
        for (Renderable renderable : layer.getRenderables()) {
            list.add(renderable);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Renderable renderable) {
        BeanNode node;
        try {
            node = new BeanNode(renderable);
        } catch (IntrospectionException ex) {
            return null;
        }

        if (renderable instanceof AVList) {
            AVList avlRen = (AVList) renderable;
            if (avlRen.hasKey(AVKey.DISPLAY_NAME)) {
                node.setName(avlRen.getStringValue(AVKey.DISPLAY_NAME));
            }
            if (avlRen.hasKey(AVKey.DISPLAY_ICON)) {
                node.setIconBaseWithExtension(avlRen.getStringValue(AVKey.DISPLAY_ICON));
            }
        }

        return node;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //this will only work if the renderable layer is extended to fire a property change
        if (evt.getPropertyName().equals("Renderables")) {
            refresh(true);
        }
    }
}
