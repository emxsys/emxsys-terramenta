package com.terramenta.actions;

import com.terramenta.interfaces.BooleanState;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Other", id = "com.terramenta.layermanager.actions.ToggleNodeAction")
@ActionRegistration(displayName = "#CTL_ToggleNodeAction", lazy = true)
@Messages("CTL_ToggleNodeAction=Toggle State")
public class ToggleNodeAction extends NodeAction {

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
     *
     * @param nodes
     */
    private void toggleNodes(Node[] nodes) {
        for (Node node : nodes) {

            //Toggle state
            if (node instanceof BooleanState.Provider) {
                BooleanState.Provider bsp = (BooleanState.Provider) node;
                bsp.setBooleanState(!bsp.getBooleanState());
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
            if (!(node instanceof BooleanState.Provider)) {
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
        return Bundle.CTL_ToggleNodeAction();
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
