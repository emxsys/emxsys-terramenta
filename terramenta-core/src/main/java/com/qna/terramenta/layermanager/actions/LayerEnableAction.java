package com.qna.terramenta.layermanager.actions;

import gov.nasa.worldwind.layers.Layer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Other", id = "com.qna.terramenta.layermanager.actions.LayerEnableAction")
@ActionRegistration(displayName = "#CTL_LayerEnableAction")
@Messages("CTL_LayerEnableAction=Toggle Layer Enabled")
public class LayerEnableAction extends NodeAction {

    /**
     * 
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        toggleNodes(nodes);
    }

    /**
     * reiterative node toggle
     * @param nodes
     */
    private void toggleNodes(Node[] nodes) {
        try {
            for (Node node : nodes) {
                InstanceCookie ic = node.getLookup().lookup(InstanceCookie.class);
                if (ic != null) {
                    Layer layer = (Layer) ic.instanceCreate();
                    layer.setEnabled(!layer.isEnabled());
                } else {
                    Node[] childNodes = node.getChildren().getNodes();
                    if (childNodes != null) {
                        toggleNodes(childNodes);
                    }
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    /**
     * 
     * @return
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }

    /**
     *
     * @param nodes
     * @return
     */
    @Override
    protected boolean enable(Node[] nodes) {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(LayerEnableAction.class, "CTL_LayerEnableAction");
    }

    /**
     *
     * @return
     */
    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
