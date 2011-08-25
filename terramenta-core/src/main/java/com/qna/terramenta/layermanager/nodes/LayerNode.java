package com.qna.terramenta.layermanager.nodes;

import com.qna.terramenta.layermanager.actions.LayerDeleteAction;
import com.qna.terramenta.layermanager.actions.LayerEnableAction;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.util.WeakListeners;
import org.openide.util.actions.SystemAction;

/**
 * 
 * @author heidtmare
 */
public class LayerNode extends BeanNode implements PropertyChangeListener {

    private String ENABLED_ICON_BASE = "images/bullet_green.png";
    private String DISABLED_ICON_BASE = "images/bullet_black.png";

    /**
     *
     * @param layer 
     * @throws IntrospectionException
     */
    public LayerNode(Layer layer) throws IntrospectionException {
        super(layer);
        this.setIconBaseWithExtension(layer.isEnabled() ? ENABLED_ICON_BASE : DISABLED_ICON_BASE);
        this.setSynchronizeName(true);
        if (layer instanceof RenderableLayer) {
            this.setChildren(new LayerChildren((RenderableLayer) layer));
        }
        layer.addPropertyChangeListener(WeakListeners.propertyChange(this, layer));
    }

    /**
     * 
     * @return
     */
    @Override
    public String getHtmlDisplayName() {
        Layer layer = (Layer) this.getBean();
        if (layer.isEnabled()) {
            return this.getName();
        } else {
            return "<font color='AAAAAA'><i>" + this.getName() + "</i></font>";
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Action getPreferredAction() {
        return SystemAction.get(LayerEnableAction.class);
    }

    /**
     * 
     * @param bln
     * @return
     */
    @Override
    public Action[] getActions(boolean bln) {
        Action[] actions = new Action[]{
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class),
            null,
            SystemAction.get(RenameAction.class),
            SystemAction.get(LayerEnableAction.class),
            SystemAction.get(LayerDeleteAction.class),
            null,
            SystemAction.get(PropertiesAction.class)
        };
        return actions;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("Enabled".equals(evt.getPropertyName())) {
            if (evt.getNewValue().equals(Boolean.TRUE)) {
                this.setIconBaseWithExtension(ENABLED_ICON_BASE);
            } else {
                this.setIconBaseWithExtension(DISABLED_ICON_BASE);
            }
            //this.fireIconChange();
            this.fireDisplayNameChange(null, getDisplayName());
        }
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean canCut() {
        return true;
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean canCopy() {
        return true;
    }
}
