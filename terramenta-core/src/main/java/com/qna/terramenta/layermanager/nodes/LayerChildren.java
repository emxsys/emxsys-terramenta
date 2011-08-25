/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.layermanager.nodes;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Renderable;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Index;
import org.openide.util.Exceptions;

/**
 *
 * @author heidtmare
 */
public class LayerChildren extends Index.ArrayChildren {

    private final RenderableLayer layer;

    /**
     * 
     * @param layer 
     */
    public LayerChildren(RenderableLayer layer) {
        super();
        this.layer = layer;
    }

    /**
     * 
     * @return
     */
    @Override
    protected java.util.List initCollection() {
        ArrayList childrenNodes = new ArrayList();
        Iterable<Renderable> renderables = layer.getRenderables();
        AVList avlRen;
        for (Renderable renderable : renderables) {
            try {
                BeanNode beanNode = new BeanNode(renderable);
                if (renderable instanceof AVList) {
                    avlRen = (AVList) renderable;
                    if (avlRen.hasKey(AVKey.DISPLAY_NAME)) {
                        beanNode.setName(avlRen.getStringValue(AVKey.DISPLAY_NAME));
                    }
                    if (avlRen.hasKey(AVKey.DISPLAY_ICON)) {
                        beanNode.setIconBaseWithExtension(avlRen.getStringValue(AVKey.DISPLAY_ICON));
                    }
                }
                childrenNodes.add(beanNode);
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return childrenNodes;
    }
}
