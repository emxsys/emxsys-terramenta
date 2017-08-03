/**
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
package com.terramenta.time.timeline;

import com.terramenta.ribbon.RibbonActionReference;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.openide.awt.ActionID;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "TimelineTopComponent",
        iconBase = "com/terramenta/time/timeline/timeline.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "timelineMode", openAtStartup = true)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_TimelineAction",
        preferredID = "TimelineTopComponent"
)
@ActionID(category = "Window", id = "com.terramenta.time.timeline.TimelineTopComponent")
@RibbonActionReference(
        path = "Ribbon/TaskPanes/Window",
        tooltipIcon = "com/terramenta/time/timeline/timeline24.png",
        position = 3,
        tooltipTitle = "Timeline Controller",
        tooltipBody = "Display the Timeline controller"
)
@Messages({
    "CTL_TimelineAction=Timeline",
    "CTL_TimelineTopComponent=Timeline",
    "HINT_TimelineTopComponent=This is the Timeline window"
})
public final class TimelineTopComponent extends TopComponent {

    private static final TimelineProvider tp = Lookup.getDefault().lookup(TimelineProvider.class);
    private transient Timeline timeline;

    public TimelineTopComponent() {
        initComponents();
        setName(Bundle.CTL_TimelineTopComponent());
        setToolTipText(Bundle.HINT_TimelineTopComponent());
        setMinimumSize(new Dimension(50, 50));
        setPreferredSize(new Dimension(75, 75));
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.TRUE);

        Platform.runLater(() -> {
            timeline = tp.getTimeline();
            jFXPanel1.setScene(new Scene(new StackPane(timeline), Color.BLACK));
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFXPanel1 = new javafx.embed.swing.JFXPanel();

        setLayout(new java.awt.BorderLayout());
        add(jFXPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javafx.embed.swing.JFXPanel jFXPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        Platform.runLater(() -> {
            timeline.setArmed(true);
        });
    }

    @Override
    public void componentClosed() {
        Platform.runLater(() -> {
            timeline.setArmed(false);
        });
    }

    public Timeline getTimeline() {
        return timeline;
    }
}
