package com.qna.terramenta.layermanager.nodes;

import com.qna.terramenta.interfaces.BooleanState;
import com.qna.terramenta.actions.ToggleBooleanStateAction;
import gov.nasa.worldwind.ogc.kml.KMLAbstractFeature;
import java.beans.IntrospectionException;
import javax.swing.Action;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.actions.SystemAction;

/**
 * 
 * @author heidtmare
 */
public class KMLFeatureNode extends BeanNode implements BooleanState.Provider {

    public static final String ENABLED_ICON_BASE = "images/bulletGreen.png";
    public static final String DISABLED_ICON_BASE = "images/bulletBlack.png";

    public KMLFeatureNode(KMLAbstractFeature feature, Children children) throws IntrospectionException {
        super(feature, children);

        if (feature.getVisibility() == null) {
            feature.setVisibility(true);
        }

        setBooleanState(feature.getVisibility());
    }

    @Override
    public boolean getBooleanState() {
        return ((KMLAbstractFeature) this.getBean()).getVisibility();
    }

    @Override
    public void setBooleanState(boolean enabled) {
        ((KMLAbstractFeature) this.getBean()).setVisibility(enabled);
        this.setIconBaseWithExtension(enabled ? ENABLED_ICON_BASE : DISABLED_ICON_BASE);
        this.fireDisplayNameChange(null, this.getDisplayName());//force node refresh
    }

    /**
     * 
     * @return
     */
    @Override
    public String getHtmlDisplayName() {
        KMLAbstractFeature feature = (KMLAbstractFeature) this.getBean();
        if (feature.getVisibility()) {
            return this.getName();
        } else {
            return "<font color='AAAAAA'><i>" + this.getName() + "</i></font>";
        }
    }

    /**
     *good idea
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(ToggleBooleanStateAction.class);
    }

    /**
     * 
     * @param bln
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(PropertiesAction.class)
        };
        return actions;
    }
}
