/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.layermanager.nodes;

import com.qna.terramenta.layermanager.actions.LayerAddAction;
import com.qna.terramenta.actions.DestroyAction;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author heidtmare
 */
public class RootNode extends AbstractNode {

    private String ICON_PATH = "images/layers.png";

    /**
     *
     * @param children 
     */
    public RootNode(Children children) {
        super(children, Lookups.singleton(children));
        setName("LAYERS");
        setDisplayName("Layers");
        setIconBaseWithExtension(ICON_PATH);
    }

    /**
     * 
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(LayerAddAction.class);
    }

    /**
     * 
     * @param bln
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(LayerAddAction.class),
            SystemAction.get(DestroyAction.class)
        };
        return actions;
    }
}
