package com.qna.terramenta.actions;

import com.qna.terramenta.interfaces.Destroyable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Other", id = "com.qna.terramenta.layermanager.actions.DestroyNodeAction")
@ActionRegistration(displayName = "#CTL_DestroyNodeAction")
@Messages("CTL_DestroyNodeAction=Remove")
public class DestroyNodeAction extends NodeAction {

    /**
     * 
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        NotifyDescriptor.Confirmation msg = new NotifyDescriptor.Confirmation(
                "Are you sure you want to remove the selected?",
                "Delete",
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
        for (Node node : nodes) {
            //Destroy
            if (node instanceof Destroyable) {
                Destroyable des = (Destroyable) node;
                des.doDestroy();
            }
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
        for (Node node : nodes) {
            if (!(node instanceof Destroyable)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(DestroyNodeAction.class, "CTL_DestroyNodeAction");
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
