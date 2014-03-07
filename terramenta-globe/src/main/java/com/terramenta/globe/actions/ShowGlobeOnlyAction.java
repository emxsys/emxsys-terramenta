/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.actions;

import com.terramenta.globe.GlobeTopComponent;
import com.terramenta.ribbon.RibbonActionReference;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author chris.heidt
 */
@ActionID(category = "Tools", id = "com.terramenta.globe.actions.ShowGlobeOnlyAction")
@ActionRegistration(iconBase = "com/terramenta/globe/images/fullGlobe.png", displayName = "#CTL_ShowGlobeOnlyAction", popupText = "Maximize the Globe.")
@RibbonActionReference(path = "Menu/Window/Layout",
        position = 100,
        priority = "top",
        description = "#CTL_ShowGlobeOnlyAction_Hint",
        tooltipTitle = "#CTL_ShowGlobeOnlyAction_TooltipTitle",
        tooltipBody = "#CTL_ShowGlobeOnlyAction_TooltipBody",
        tooltipIcon = "com/terramenta/globe/images/fullGlobe32.png",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_ShowGlobeOnlyAction=Toggle Full Globe",
    "CTL_ShowGlobeOnlyAction_Hint=Toggle Full Globe.",
    "CTL_ShowGlobeOnlyAction_TooltipTitle=Toggle Full Globe",
    "CTL_ShowGlobeOnlyAction_TooltipBody=Toggles the Globe between Maximized and Minimizes views.",})
public class ShowGlobeOnlyAction extends AbstractAction implements PropertyChangeListener {

    private Container originalContentPane = null;

    private ShowGlobeOnlyAction() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }

    /**
     * Toggle enabled and disabled based on if globe window opens/closes
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_OPENED) || evt.getPropertyName().equals(TopComponent.Registry.PROP_TC_CLOSED)) {
            if (evt.getNewValue() instanceof GlobeTopComponent) {
                GlobeTopComponent gtc = (GlobeTopComponent) evt.getNewValue();
                setEnabled(gtc.isOpened());
            }
        }
    }

    /**
     * When the btn is clicked
     *
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (originalContentPane == null) {
            activate();
        } else {
            deactivate();
        }
    }

    private void activate() {
        assert null == originalContentPane;

        TopComponent globeTopComponent = WindowManager.getDefault().findTopComponent("GlobeTopComponent");
        if (globeTopComponent == null || !globeTopComponent.isOpened()) {
            return;
        }

        final JFrame mainWnd = (JFrame) WindowManager.getDefault().getMainWindow();
        originalContentPane = mainWnd.getContentPane();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(globeTopComponent, BorderLayout.CENTER);
        mainWnd.setContentPane(panel);
        mainWnd.invalidate();
        mainWnd.revalidate();
        mainWnd.repaint();

        refocus();
    }

    private void deactivate() {
        JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
        frame.setContentPane(originalContentPane);
        originalContentPane = null;

        refocus();
    }

    private void refocus() {
        final TopComponent tc = TopComponent.getRegistry().getActivated();
        if (tc == null) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tc.requestFocusInWindow();
            }
        });
    }
}
