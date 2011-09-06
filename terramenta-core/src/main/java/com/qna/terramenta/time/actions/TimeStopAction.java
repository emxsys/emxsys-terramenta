/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.time.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Other", id = "com.qna.terramenta.time.actions.TimeStopAction")
@ActionRegistration(iconBase = "images/controlStop.png", displayName = "#CTL_TimeStopAction")
@ActionReferences({
    @ActionReference(path = "Menu/Time", position = 5),
    @ActionReference(path = "Toolbars/Time", position = 3)
})
@Messages("CTL_TimeStopAction=Stop")
public final class TimeStopAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TimeActionController.stop();
    }
}

/*@ActionID(category = "Tools",
id = "some.module.SomeAction")
@ActionRegistration(iconBase = "some/module/someaction.png",
displayName = "#CTL_SomeAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 2030, separatorAfter = 2031)
})
@Messages("CTL_SomeAction=Some Action")
public final class SomeAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private static final LicenseService licenseService = LicenseService.getDefault();
    private static final Lookup.Result<License> licenseResult = licenseService.getLookup().lookupResult(License.class);
    private final String[] LICENSE_REQUIREMENTS = new String[]{"some_feature", "another_feature"}; // either of these should cause feature to be enabled 

    public SomeAction() {
        super();
        setEnabled(checkEnabled());
        licenseResult.addLookupListener(this);
        resultChanged(null);
    }

    private boolean checkEnabled() {
        return licenseService.isAtLeastOneFeaturePresent(LICENSE_REQUIREMENTS);
    }

    public void actionPerformed(ActionEvent e) {
// perform some special action ... 
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(checkEnabled());
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
// ignoring actionContext here, as we depend on a predetermined Lookup (provided by LicenseService) 
        return new SomeAction();
    }
}*/
