/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.layermanager.actions;

import com.qna.terramenta.globe.WorldWindManager;
import com.qna.terramenta.layermanager.LayerSelectorPanel;
import java.awt.event.ActionEvent;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author heidtmare
 */
@ActionID(category = "Other", id = "com.qna.terramenta.layermanager.actions.LayerAddAction")
@ActionRegistration(displayName = "#CTL_LayerAddAction")
@Messages("CTL_LayerAddAction=Add Layer")
public class LayerAddAction extends SystemAction {

    private static final WorldWindManager wwm = WorldWindManager.getInstance();

    /**
     *
     * @param evt
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        LayerSelectorPanel ls = new LayerSelectorPanel();
        DialogDescriptor d = new DialogDescriptor(ls, "Add Layer");
        Object result = DialogDisplayer.getDefault().notify(d);
        //was cancel hit?
        if (result != NotifyDescriptor.OK_OPTION) {
            return;
        }
        wwm.getWorldWindow().getModel().getLayers().add(ls.getLayer());
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return NbBundle.getMessage(LayerAddAction.class, "CTL_LayerAddAction");
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
