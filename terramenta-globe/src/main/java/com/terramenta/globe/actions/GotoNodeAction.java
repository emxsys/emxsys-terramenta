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
package com.terramenta.globe.actions;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.globe.interfaces.PositionProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.InstanceCookie;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author heidtmare
 */
@ActionID(id = "com.terramenta.actions.GotoNodeAction", category = "Other")
@ActionRegistration(displayName = "#CTL_GotoNodeAction", lazy = true)
@Messages("CTL_GotoNodeAction=Go To")
public class GotoNodeAction extends NodeAction {

    private static final Logger logger = Logger.getLogger(GotoNodeAction.class.getName());
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    /**
     *
     * @param nodes
     */
    @Override
    protected void performAction(Node[] nodes) {
        for (Node node : nodes) {
            try {
                Object instance = node.getLookup().lookup(InstanceCookie.class).instanceCreate();
                if (instance instanceof PositionProvider) {
                    gov.nasa.worldwind.geom.Position position = ((PositionProvider) instance).getPosition();
                    if (position != null) {
                        wwm.gotoPosition(position);
                    }
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
        if (nodes.length == 1) {
            try {
                Object instance = nodes[0].getLookup().lookup(InstanceCookie.class).instanceCreate();
                if (instance instanceof PositionProvider) {
                    return true;
                }
            } catch (Exception ex) {
                //...
            }
        }
        return false;
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
