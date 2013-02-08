/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.actions;

import com.terramenta.globe.options.GlobeOptions;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.actions.BooleanStateAction;

@ActionID(category = "Other", id = "com.terramenta.globe.actions.EarthRotationAction")
@ActionRegistration(displayName = "#CTL_EarthRotationAction", popupText = "#HINT_EarthRotationAction", lazy = true)
@ActionReferences({
    @ActionReference(path = "Menu/Animate", position = 1000),
    @ActionReference(path = "Shortcuts", name = "DS-E")
})
@Messages({
    "CTL_EarthRotationAction=ECI/ECEF",
    "HINT_EarthRotationAction=Toggle between \"Earth-centered inertial\" and \"Earth-centered, Earth-fixed(ECEF)\" views."
})
public final class EarthRotationAction extends BooleanStateAction implements PropertyChangeListener {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);

    public EarthRotationAction() {
        setBooleanState(prefs.getBoolean("options.globe.isECI", false));
        addPropertyChangeListener(this);
    }

    @Override
    public String getName() {
        return Bundle.CTL_EarthRotationAction();
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(PROP_BOOLEAN_STATE)) {
            Boolean state = (Boolean) evt.getNewValue();
            prefs.putBoolean("options.globe.isECI", state);
        }
    }
}
