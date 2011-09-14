/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.globe.actions;

import com.qna.terramenta.globe.options.GlobeOptions;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.actions.BooleanStateAction;

@ActionID(category = "View", id = "com.qna.terramenta.globe.actions.EarthRotationAction")
@ActionRegistration(displayName = "#CTL_EarthRotationAction")
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 1000),
    @ActionReference(path = "Shortcuts", name = "DS-R")
})
@Messages("CTL_EarthRotationAction=Show ECI")
public final class EarthRotationAction extends BooleanStateAction implements PropertyChangeListener {

    private static final Preferences prefs = NbPreferences.forModule(GlobeOptions.class);
    private static final ImageIcon icon = new ImageIcon(ImageUtilities.loadImage("images/tick.png", true));

    @Override
    protected void initialize() {
        super.initialize();
        setBooleanState(false);
        addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BooleanStateAction.PROP_BOOLEAN_STATE)) {
            boolean selected = (Boolean) evt.getNewValue();
            prefs.putBoolean("options.globe.isECI", selected);
        }
    }

    @Override
    protected String iconResource() {
        return getBooleanState() ? "images/tick.png" : "org/netbeans/modules/form/resources/selection_mode.png";
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(EarthRotationAction.class, "CTL_EarthRotationAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
}
