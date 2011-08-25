package com.qna.terramenta.layermanager.actions;

import com.qna.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.layers.Layer;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
public class LayerDeleteAction extends NodeAction {

    private static final WorldWindManager wwm = WorldWindManager.getInstance();

    /**
     * 
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        NotifyDescriptor.Confirmation msg = new NotifyDescriptor.Confirmation(
                "Are you sure you want to remove the selected layers?",
                "Delete Layer",
                NotifyDescriptor.OK_CANCEL_OPTION);
        Object result = DialogDisplayer.getDefault().notify(msg);
        if (NotifyDescriptor.YES_OPTION.equals(result)) {
            deleteNodes(nodes);
        }
    }

    /**
     * reiterative node deletion
     * @param nodes
     */
    private void deleteNodes(Node[] nodes) {
        try {
            for (Node node : nodes) {
                InstanceCookie ic = node.getLookup().lookup(InstanceCookie.class);
                if (ic != null) {
                    wwm.getLayers().remove((Layer) ic.instanceCreate());
                } else {
                    Node[] childNodes = node.getChildren().getNodes();
                    if (childNodes != null) {
                        deleteNodes(childNodes);
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
        return NbBundle.getMessage(LayerDeleteAction.class, "CTL_LayerDeleteAction");
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
