/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.options;

import com.terramenta.utilities.Classification;
import com.terramenta.globe.utilities.CoordinateSystem;
import gov.nasa.worldwind.avlist.AVKey;
import java.util.prefs.Preferences;
import javax.swing.ButtonModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.util.NbPreferences;

/**
 *
 * @author heidtmare
 */
public final class GlobeOptions extends JPanel {

    private Preferences pref = NbPreferences.forModule(GlobeOptions.class);

    /**
     *
     */
    public GlobeOptions() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        statusBarButtonGroup = new javax.swing.ButtonGroup();
        displayModeButtonGroup = new javax.swing.ButtonGroup();
        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        worldwindConfigTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        classificationCombo = new javax.swing.JComboBox(Classification.values());
        jLabel3 = new javax.swing.JLabel();
        classificationTextField = new javax.swing.JTextField();
        flatEarthPanel = new javax.swing.JPanel();
        useFlatEarthCheckBox = new javax.swing.JCheckBox();
        flatProjectionComboBox = new javax.swing.JComboBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        statusBarPanel = new javax.swing.JPanel();
        statusBarStandardRadioButton = new javax.swing.JRadioButton();
        statusBarUtmRadioButton = new javax.swing.JRadioButton();
        statusBarMgrsRadioButton = new javax.swing.JRadioButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        viewPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        bStereoNone = new javax.swing.JRadioButton();
        bStereoRedBlue = new javax.swing.JRadioButton();
        bStereoDevice = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        focusAngleSlider = new javax.swing.JSlider();
        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        quickTipsCheckBox = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();

        jFileChooser1.setFileFilter(new FileNameExtensionFilter("xml files", "xml"));

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jPanel4.border.title"))); // NOI18N
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1);

        worldwindConfigTextField.setText(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.worldwindConfigTextField.text")); // NOI18N
        worldwindConfigTextField.setMaximumSize(new java.awt.Dimension(2147483647, 25));
        worldwindConfigTextField.setMinimumSize(new java.awt.Dimension(100, 25));
        worldwindConfigTextField.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel4.add(worldwindConfigTextField);

        jLabel1.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jLabel1.text")); // NOI18N
        jPanel4.add(jLabel1);

        add(jPanel4);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jPanel2.border.title"))); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(539, 54));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        classificationCombo.setBorder(null);
        classificationCombo.setMaximumSize(new java.awt.Dimension(32767, 25));
        classificationCombo.setMinimumSize(new java.awt.Dimension(110, 25));
        classificationCombo.setPreferredSize(new java.awt.Dimension(110, 25));
        classificationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                classificationComboActionPerformed(evt);
            }
        });
        jPanel2.add(classificationCombo);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jLabel3.text")); // NOI18N
        jPanel2.add(jLabel3);

        classificationTextField.setText(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.classificationTextField.text")); // NOI18N
        classificationTextField.setMaximumSize(new java.awt.Dimension(2147483647, 25));
        classificationTextField.setMinimumSize(new java.awt.Dimension(100, 25));
        classificationTextField.setPreferredSize(new java.awt.Dimension(100, 25));
        jPanel2.add(classificationTextField);

        add(jPanel2);

        flatEarthPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.flatEarthPanel.border.title"))); // NOI18N
        flatEarthPanel.setLayout(new javax.swing.BoxLayout(flatEarthPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(useFlatEarthCheckBox, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.useFlatEarthCheckBox.text")); // NOI18N
        flatEarthPanel.add(useFlatEarthCheckBox);

        flatProjectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mercator", "Lat Lon", "Sinusoidal", "Modified Sinusoidal" }));
        flatProjectionComboBox.setBorder(null);
        flatProjectionComboBox.setLightWeightPopupEnabled(false);
        flatProjectionComboBox.setMaximumSize(new java.awt.Dimension(120, 25));
        flatProjectionComboBox.setMinimumSize(new java.awt.Dimension(120, 25));
        flatProjectionComboBox.setPreferredSize(new java.awt.Dimension(120, 25));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, useFlatEarthCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), flatProjectionComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        flatEarthPanel.add(flatProjectionComboBox);
        flatEarthPanel.add(filler2);

        add(flatEarthPanel);

        statusBarPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarPanel.border.title"))); // NOI18N
        statusBarPanel.setLayout(new javax.swing.BoxLayout(statusBarPanel, javax.swing.BoxLayout.LINE_AXIS));

        statusBarButtonGroup.add(statusBarStandardRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(statusBarStandardRadioButton, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarStandardRadioButton.text")); // NOI18N
        statusBarStandardRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarStandardRadioButton.actionCommand")); // NOI18N
        statusBarPanel.add(statusBarStandardRadioButton);

        statusBarButtonGroup.add(statusBarUtmRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(statusBarUtmRadioButton, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarUtmRadioButton.text")); // NOI18N
        statusBarUtmRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarUtmRadioButton.actionCommand")); // NOI18N
        statusBarPanel.add(statusBarUtmRadioButton);

        statusBarButtonGroup.add(statusBarMgrsRadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(statusBarMgrsRadioButton, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarMgrsRadioButton.text")); // NOI18N
        statusBarMgrsRadioButton.setActionCommand(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.statusBarMgrsRadioButton.actionCommand")); // NOI18N
        statusBarPanel.add(statusBarMgrsRadioButton);
        statusBarPanel.add(filler1);

        add(statusBarPanel);

        viewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.viewPanel.border.title"))); // NOI18N
        viewPanel.setLayout(new javax.swing.BoxLayout(viewPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        bStereoNone.setActionCommand(AVKey.STEREO_MODE_NONE);
        displayModeButtonGroup.add(bStereoNone);
        org.openide.awt.Mnemonics.setLocalizedText(bStereoNone, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.bStereoNone.text")); // NOI18N
        jPanel6.add(bStereoNone);

        bStereoRedBlue.setActionCommand(AVKey.STEREO_MODE_RED_BLUE);
        displayModeButtonGroup.add(bStereoRedBlue);
        org.openide.awt.Mnemonics.setLocalizedText(bStereoRedBlue, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.bStereoRedBlue.text")); // NOI18N
        jPanel6.add(bStereoRedBlue);

        bStereoDevice.setActionCommand(AVKey.STEREO_MODE_DEVICE);
        displayModeButtonGroup.add(bStereoDevice);
        org.openide.awt.Mnemonics.setLocalizedText(bStereoDevice, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.bStereoDevice.text")); // NOI18N
        jPanel6.add(bStereoDevice);

        viewPanel.add(jPanel6);

        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jLabel2.text")); // NOI18N
        jPanel7.add(jLabel2);

        focusAngleSlider.setMajorTickSpacing(10);
        focusAngleSlider.setMinorTickSpacing(1);
        focusAngleSlider.setPaintTicks(true);
        focusAngleSlider.setSnapToTicks(true);
        focusAngleSlider.setToolTipText(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.focusAngleSlider.toolTipText")); // NOI18N
        jPanel7.add(focusAngleSlider);

        viewPanel.add(jPanel7);

        add(viewPanel);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));
        add(jPanel5);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.jPanel1.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(quickTipsCheckBox, org.openide.util.NbBundle.getMessage(GlobeOptions.class, "GlobeOptions.quickTipsCheckBox.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(quickTipsCheckBox)
                .addContainerGap(415, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(quickTipsCheckBox)
        );

        add(jPanel1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 518, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 451, Short.MAX_VALUE)
        );

        add(jPanel3);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (JFileChooser.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
            worldwindConfigTextField.setText(jFileChooser1.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void classificationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_classificationComboActionPerformed
        classificationTextField.setEnabled(!classificationCombo.getSelectedItem().equals("None"));
    }//GEN-LAST:event_classificationComboActionPerformed

    /**
     *
     */
    public void load() {
        //Flat Earth
        boolean flat = Boolean.parseBoolean(pref.get("options.globe.isFlat", "false"));
        useFlatEarthCheckBox.setSelected(flat);
        flatProjectionComboBox.setSelectedItem(pref.get("options.globe.flatProjection", "Lat Lon"));

        //View
        String dm = pref.get("options.globe.displayMode", AVKey.STEREO_MODE_NONE);
        ButtonModel dmmodel;
        if (dm.equalsIgnoreCase(AVKey.STEREO_MODE_RED_BLUE)) {
            dmmodel = bStereoRedBlue.getModel();
        } else if (dm.equalsIgnoreCase(AVKey.STEREO_MODE_DEVICE)) {
            dmmodel = bStereoDevice.getModel();
        } else {
            dmmodel = bStereoNone.getModel();
        }
        displayModeButtonGroup.setSelected(dmmodel, true);
        focusAngleSlider.setValue(pref.getInt("options.globe.focusAngle", 0));

        //Status Bar
        ButtonModel model = statusBarStandardRadioButton.getModel();
        CoordinateSystem cs = CoordinateSystem.valueOf(pref.get("options.globe.statusBar", CoordinateSystem.LatLon.name()));
        if (cs.equals(CoordinateSystem.UTM)) {
            model = statusBarUtmRadioButton.getModel();
        } else if (cs.equals(CoordinateSystem.MGRS)) {
            model = statusBarMgrsRadioButton.getModel();
        }
        statusBarButtonGroup.setSelected(model, true);

        //WorldWind Config
        worldwindConfigTextField.setText(pref.get("options.globe.worldwindConfig", ""));

        //ToolTips
        quickTipsCheckBox.setSelected(pref.getBoolean("options.globe.quickTips", true));

        //Classification Banner
        classificationCombo.setSelectedItem(Classification.valueOf(pref.get("options.globe.classification", "UNCLASSIFIED")));
        classificationTextField.setText(pref.get("options.globe.protection", ""));
    }

    /**
     *
     */
    public void store() {
        //Flat Earth
        pref.putBoolean("options.globe.isFlat", useFlatEarthCheckBox.isSelected());
        pref.put("options.globe.flatProjection", flatProjectionComboBox.getSelectedItem().toString());

        //View
        pref.put("options.globe.displayMode", displayModeButtonGroup.getSelection().getActionCommand());
        pref.putInt("options.globe.focusAngle", focusAngleSlider.getValue());

        //Status Bar
        pref.put("options.globe.statusBar", statusBarButtonGroup.getSelection().getActionCommand());

        //WorldWind Config
        pref.put("options.globe.worldwindConfig", worldwindConfigTextField.getText());

        //ToolTips
        pref.putBoolean("options.globe.quickTips", quickTipsCheckBox.isSelected());

        //Classification Banner
        pref.put("options.globe.classification", classificationCombo.getSelectedItem().toString());
        pref.put("options.globe.protection", classificationTextField.getText());
    }

    /**
     *
     * @return
     */
    public boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton bStereoDevice;
    private javax.swing.JRadioButton bStereoNone;
    private javax.swing.JRadioButton bStereoRedBlue;
    private javax.swing.JComboBox classificationCombo;
    private javax.swing.JTextField classificationTextField;
    private javax.swing.ButtonGroup displayModeButtonGroup;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JPanel flatEarthPanel;
    private javax.swing.JComboBox flatProjectionComboBox;
    private javax.swing.JSlider focusAngleSlider;
    private javax.swing.JButton jButton1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JCheckBox quickTipsCheckBox;
    private javax.swing.ButtonGroup statusBarButtonGroup;
    private javax.swing.JRadioButton statusBarMgrsRadioButton;
    private javax.swing.JPanel statusBarPanel;
    private javax.swing.JRadioButton statusBarStandardRadioButton;
    private javax.swing.JRadioButton statusBarUtmRadioButton;
    private javax.swing.JCheckBox useFlatEarthCheckBox;
    private javax.swing.JPanel viewPanel;
    private javax.swing.JTextField worldwindConfigTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
