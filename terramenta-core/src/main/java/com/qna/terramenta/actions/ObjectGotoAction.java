package com.qna.terramenta.actions;

import com.qna.terramenta.interfaces.Geospatial;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(id = "com.qna.terramenta.actions.ObjectGotoAction", category = "Other")
@ActionRegistration(displayName = "#CTL_ObjectGotoAction")
public class ObjectGotoAction extends NodeAction {

    private static final Logger logger = Logger.getLogger(ObjectGotoAction.class.getName());

    /**
     * 
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            try {
                Object instance = node.getLookup().lookup(InstanceCookie.class).instanceCreate();
                if (instance instanceof Geospatial) {
                    this.firePropertyChange("GOTO", null, ((Geospatial) instance).getPosition());
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
        return NbBundle.getMessage(ObjectGotoAction.class, "CTL_ObjectGotoAction");
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
