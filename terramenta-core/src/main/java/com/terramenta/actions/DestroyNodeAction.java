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
package com.terramenta.actions;

import com.terramenta.interfaces.Destroyable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
@ActionID(category = "Other", id = "com.terramenta.layermanager.actions.DestroyNodeAction")
@ActionRegistration(displayName = "#CTL_DestroyNodeAction", lazy = true)
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
     *
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
        return Bundle.CTL_DestroyNodeAction();
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
