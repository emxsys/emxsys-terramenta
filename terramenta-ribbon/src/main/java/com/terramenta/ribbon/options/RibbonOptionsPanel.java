/*
 * Copyright (c) 2014, Bruce Schubert <bruce@emxsys.com>
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * - Neither the name of Bruce Schubert, Emxsys nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terramenta.ribbon.options;

import com.terramenta.ribbon.spi.RibbonPreferencesProvider;
import java.util.prefs.Preferences;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;

final class RibbonOptionsPanel extends javax.swing.JPanel {

    private static final Preferences prefs = NbPreferences.forModule(RibbonOptions.class);
    private final RibbonOptionsPanelController controller;
    private String currentStyle;

    RibbonOptionsPanel(RibbonOptionsPanelController controller) {
        this.controller = controller;
        initComponents();
        // TODO listen to changes in form fields and call controller.changed()
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        styleButtonGroup = new javax.swing.ButtonGroup();
        stylePanel = new javax.swing.JPanel();
        fullsizeRadioButton = new javax.swing.JRadioButton();
        compactRadioButton = new javax.swing.JRadioButton();
        otherRadioButton = new javax.swing.JRadioButton();
        fullsizeLabel = new javax.swing.JLabel();
        compactLabel = new javax.swing.JLabel();
        otherLabel = new javax.swing.JLabel();

        stylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.stylePanel.border.title_1"))); // NOI18N

        styleButtonGroup.add(fullsizeRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(fullsizeRadioButton, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.fullsizeRadioButton.text")); // NOI18N

        styleButtonGroup.add(compactRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(compactRadioButton, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.compactRadioButton.text")); // NOI18N

        styleButtonGroup.add(otherRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(otherRadioButton, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.otherRadioButton.text")); // NOI18N

        fullsizeLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/terramenta/ribbon/images/office2013-fullsize-example.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(fullsizeLabel, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.fullsizeLabel.text")); // NOI18N
        fullsizeLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        fullsizeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fullsizeLabelMouseClicked(evt);
            }
        });

        compactLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/terramenta/ribbon/images/office2013-compact-example.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(compactLabel, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.compactLabel.text")); // NOI18N
        compactLabel.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 0, 0)));
        compactLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                compactLabelMouseClicked(evt);
            }
        });

        otherLabel.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(otherLabel, org.openide.util.NbBundle.getMessage(RibbonOptionsPanel.class, "RibbonOptionsPanel.otherLabel.text")); // NOI18N
        otherLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                otherLabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout stylePanelLayout = new javax.swing.GroupLayout(stylePanel);
        stylePanel.setLayout(stylePanelLayout);
        stylePanelLayout.setHorizontalGroup(
            stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stylePanelLayout.createSequentialGroup()
                .addComponent(fullsizeRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fullsizeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(stylePanelLayout.createSequentialGroup()
                .addGroup(stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(compactRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(otherRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(stylePanelLayout.createSequentialGroup()
                        .addComponent(compactLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addComponent(otherLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        stylePanelLayout.setVerticalGroup(
            stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(stylePanelLayout.createSequentialGroup()
                .addGroup(stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fullsizeRadioButton)
                    .addComponent(fullsizeLabel))
                .addGap(12, 12, 12)
                .addGroup(stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(compactRadioButton)
                    .addComponent(compactLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(stylePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(otherRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(otherLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stylePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(stylePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fullsizeLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fullsizeLabelMouseClicked
        // Select 
        fullsizeRadioButton.setSelected(true);
    }//GEN-LAST:event_fullsizeLabelMouseClicked

    private void compactLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compactLabelMouseClicked
        // Select
        compactRadioButton.setSelected(true);
    }//GEN-LAST:event_compactLabelMouseClicked

    private void otherLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_otherLabelMouseClicked
        // TODO add your handling code here:
        if (otherRadioButton.isEnabled()) {
            otherRadioButton.setSelected(true);
        }
    }//GEN-LAST:event_otherLabelMouseClicked

    void load() {

        // Enable "Other" option if a 3rd party is providing the Ribbon UI
        RibbonPreferencesProvider provider = Lookup.getDefault().lookup(RibbonPreferencesProvider.class);
        otherRadioButton.setEnabled(provider != null);
        otherLabel.setText(provider != null ? provider.getPreferences().getClass().getSimpleName() : "");
        currentStyle = prefs.get(RibbonOptions.STYLE, RibbonOptions.DEFAULT_STYLE);
        switch (currentStyle) {
            case RibbonOptions.COMPACT_STYLE:
                compactRadioButton.setSelected(true);
                break;
            case RibbonOptions.FULLSIZE_STYLE:
                fullsizeRadioButton.setSelected(true);
                break;
            case RibbonOptions.OTHER_STYLE:
                otherRadioButton.setSelected(true);
                break;
            default:
                compactRadioButton.setSelected(true);
        }
    }

    void store() {
        String newStyle;
        if (fullsizeRadioButton.isSelected()) {
            newStyle = RibbonOptions.FULLSIZE_STYLE;
        } else if (compactRadioButton.isSelected()) {
            newStyle = RibbonOptions.COMPACT_STYLE;
        } else {
            newStyle = RibbonOptions.OTHER_STYLE;
        }
        prefs.put(RibbonOptions.STYLE, newStyle);
        if (!(currentStyle.equals(newStyle)))
        {
            controller.changed();
        }
    }

    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel compactLabel;
    private javax.swing.JRadioButton compactRadioButton;
    private javax.swing.JLabel fullsizeLabel;
    private javax.swing.JRadioButton fullsizeRadioButton;
    private javax.swing.JLabel otherLabel;
    private javax.swing.JRadioButton otherRadioButton;
    private javax.swing.ButtonGroup styleButtonGroup;
    private javax.swing.JPanel stylePanel;
    // End of variables declaration//GEN-END:variables
}
