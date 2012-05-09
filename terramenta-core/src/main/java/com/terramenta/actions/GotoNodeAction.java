package com.terramenta.actions;

import com.terramenta.interfaces.Position;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(id = "com.terramenta.actions.GotoNodeAction", category = "Other")
@ActionRegistration(displayName = "#CTL_GotoNodeAction")
@Messages("CTL_GotoNodeAction=Go To")
public class GotoNodeAction extends NodeAction {

    private static final Logger logger = Logger.getLogger(GotoNodeAction.class.getName());

    /**
     *
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            try {
                Object instance = node.getLookup().lookup(InstanceCookie.class).instanceCreate();
                if (instance instanceof Position.Provider) {
                    this.firePropertyChange("GOTO", null, ((Position.Provider) instance).getPosition());
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Goto Error", ex);
            }
        }
    }

    /**
     *
     * @param nodes
     * @return
     */
    @Override
    protected boolean enable(Node[] nodes) {
        if (nodes.length != 1) {
            return false;
        }
        return true;
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
     * @return
     */
    @Override
    public String getName() {
        return Bundle.CTL_GotoNodeAction();
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