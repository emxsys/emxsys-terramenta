/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qna.terramenta.measurements;

import com.qna.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.TerrainProfileLayer;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//com.qna.terramenta.measurements//Measurements//EN", autostore = false)
@TopComponent.Description(preferredID = "MeasurementsTopComponent", iconBase = "images/pencil-ruler.png", persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "leftSlidingSide", openAtStartup = false)
@ActionID(category = "Window", id = "com.qna.terramenta.measurements.MeasurementsTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_MeasurementsAction", preferredID = "MeasurementsTopComponent")
public final class MeasurementsTopComponent extends TopComponent {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final MeasureTool measureTool = new MeasureTool(wwm.getWorldWindow());
    private static final TerrainProfileLayer profile = new TerrainProfileLayer();

    public MeasurementsTopComponent() {

        setName(NbBundle.getMessage(MeasurementsTopComponent.class, "CTL_MeasurementsTopComponent"));
        setToolTipText(NbBundle.getMessage(MeasurementsTopComponent.class, "HINT_MeasurementsTopComponent"));

        profile.setName("Annotations - Terrain Profiler");
        profile.setEventSource(wwm.getWorldWindow());
        profile.setFollow(TerrainProfileLayer.FOLLOW_PATH);
        profile.setShowProfileLine(true);

        // Handle measure tool events
        measureTool.getLayer().setName("Annotations - Measurements");
        measureTool.setFollowTerrain(true);
        measureTool.setController(new MeasureToolController());
        measureTool.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                // Add, remove or change positions
                if (event.getPropertyName().equals(MeasureTool.EVENT_POSITION_ADD)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REMOVE)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REPLACE)) {
                    fillPointsPanel();    // Update position list when changed
                    updateProfile(((MeasureTool) event.getSource()));
                } // The tool was armed / disarmed
                else if (event.getPropertyName().equals(MeasureTool.EVENT_ARMED)) {
                    if (measureTool.isArmed()) {
                        startButton.setEnabled(false);
                        pauseButton.setText("Pause");
                        pauseButton.setEnabled(true);
                        clearButton.setEnabled(true);
                        ((Component) wwm.getWorldWindow()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    } else {
                        startButton.setEnabled(true);
                        pauseButton.setText("Pause");
                        pauseButton.setEnabled(false);
                        clearButton.setEnabled(false);
                        ((Component) wwm.getWorldWindow()).setCursor(Cursor.getDefaultCursor());
                    }
                } // Metric changed - sent after each render frame
                else if (event.getPropertyName().equals(MeasureTool.EVENT_METRIC_CHANGED)) {
                    updateMetric();
                }
            }

            /**
             *
             * @param mt
             */
            private void updateProfile(MeasureTool mt) {
                ArrayList<? extends LatLon> positions = mt.getPositions();
                if (positions != null && positions.size() > 1) {
                    profile.setPathPositions(positions);
                    profile.setEnabled(true);
                } else {
                    profile.setEnabled(false);
                }
                wwm.getWorldWindow().redraw();
            }
        });

        initComponents();
    }

    /**
     *
     */
    private void fillPointsPanel() {
        int i = 0;
        if (measureTool.getPositions() != null) {
            positionLog.setText("");
            for (LatLon pos : measureTool.getPositions()) {
                String las = String.format("Lat %7.4f\u00B0", pos.getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getLongitude().getDegrees());
                positionLog.setCaretPosition(positionLog.getDocument().getLength());
                positionLog.append(las + "  " + los + "\n");
            }
        }
    }

    /**
     *
     */
    private void updateMetric() {
        // Update length label
        double value = measureTool.getLength();
        String s;
        if (value <= 0) {
            s = "na";
        } else if (value < 1000) {
            s = String.format("%,7.1f m", value);
        } else {
            s = String.format("%,7.3f km", value / 1000);
        }
        lengthValueLabel.setText(s);

        // Update area label
        value = measureTool.getArea();
        if (value < 0) {
            s = "na";
        } else if (value < 1e6) {
            s = String.format("%,7.1f m2", value);
        } else {
            s = String.format("%,7.3f km2", value / 1e6);
        }
        areaValueLabel.setText(s);

        // Update width label
        value = measureTool.getWidth();
        if (value < 0) {
            s = "na";
        } else if (value < 1000) {
            s = String.format("%,7.1f m", value);
        } else {
            s = String.format("%,7.3f km", value / 1000);
        }
        widthValueLabel.setText(s);

        // Update height label
        value = measureTool.getHeight();
        if (value < 0) {
            s = "na";
        } else if (value < 1000) {
            s = String.format("%,7.1f m", value);
        } else {
            s = String.format("%,7.3f km", value / 1000);
        }
        heightValueLabel.setText(s);

        // Update heading label
        Angle angle = measureTool.getOrientation();
        if (angle != null) {
            s = String.format("%,6.2f\u00B0", angle.degrees);
        } else {
            s = "na";
        }
        headingValueLabel.setText(s);

        // Update center label
        Position center = measureTool.getCenterPosition();
        if (center != null) {
            s = String.format("%,7.4f\u00B0 %,7.4f\u00B0", center.getLatitude().degrees, center.getLongitude().degrees);
        } else {
            s = "na";
        }
        centerValueLabel.setText(s);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        metricsPanel = new javax.swing.JPanel();
        lengthLabel = new javax.swing.JLabel();
        areaLabel = new javax.swing.JLabel();
        widthLabel = new javax.swing.JLabel();
        heightLabel = new javax.swing.JLabel();
        headingLabel = new javax.swing.JLabel();
        centerLabel = new javax.swing.JLabel();
        lengthValueLabel = new javax.swing.JLabel();
        areaValueLabel = new javax.swing.JLabel();
        widthValueLabel = new javax.swing.JLabel();
        heightValueLabel = new javax.swing.JLabel();
        headingValueLabel = new javax.swing.JLabel();
        centerValueLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        positionLog = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        shapeCombo = new javax.swing.JComboBox();
        startButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
        pathCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        followTerrainCheckBox = new javax.swing.JCheckBox();
        rubberbandCheckBox = new javax.swing.JCheckBox();
        tooltipsCheckBox = new javax.swing.JCheckBox();
        pointsCheckBox = new javax.swing.JCheckBox();
        freeHandCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        lineColorButton = new javax.swing.JButton();
        pointsColorButton = new javax.swing.JButton();
        tooltipsColorButton = new javax.swing.JButton();

        metricsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.metricsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lengthLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.lengthLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(areaLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.areaLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(widthLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.widthLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(heightLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.heightLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headingLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.headingLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(centerLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.centerLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lengthValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.lengthValueLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(areaValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.areaValueLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(widthValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.widthValueLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(heightValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.heightValueLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(headingValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.headingValueLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(centerValueLabel, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.centerValueLabel.text")); // NOI18N

        javax.swing.GroupLayout metricsPanelLayout = new javax.swing.GroupLayout(metricsPanel);
        metricsPanel.setLayout(metricsPanelLayout);
        metricsPanelLayout.setHorizontalGroup(
            metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(metricsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lengthLabel)
                    .addComponent(areaLabel)
                    .addComponent(widthLabel)
                    .addComponent(heightLabel)
                    .addComponent(headingLabel)
                    .addComponent(centerLabel))
                .addGap(10, 10, 10)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(centerValueLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(headingValueLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(heightValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(areaValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(lengthValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                    .addComponent(widthValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                .addContainerGap())
        );
        metricsPanelLayout.setVerticalGroup(
            metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(metricsPanelLayout.createSequentialGroup()
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lengthLabel)
                    .addComponent(lengthValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(areaLabel)
                    .addComponent(areaValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(widthLabel)
                    .addComponent(widthValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightLabel)
                    .addComponent(heightValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(headingLabel)
                    .addComponent(headingValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(metricsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(centerLabel)
                    .addComponent(centerValueLabel)))
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.jScrollPane1.border.title"))); // NOI18N

        positionLog.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        positionLog.setColumns(20);
        positionLog.setRows(5);
        jScrollPane1.setViewportView(positionLog);

        shapeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Line", "Path", "Polygon", "Circle", "Ellipse", "Square", "Rectangle" }));
        shapeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapeComboActionPerformed(evt);
            }
        });
        buttonPanel.add(shapeCombo);

        org.openide.awt.Mnemonics.setLocalizedText(startButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.startButton.text")); // NOI18N
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(startButton);

        org.openide.awt.Mnemonics.setLocalizedText(pauseButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.pauseButton.text")); // NOI18N
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(pauseButton);

        org.openide.awt.Mnemonics.setLocalizedText(clearButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.clearButton.text")); // NOI18N
        clearButton.setEnabled(false);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(clearButton);

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.settingsPanel.border.title"))); // NOI18N

        pathCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Great circle", "Linear", "Rhumb" }));
        pathCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pathComboActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.jLabel2.text")); // NOI18N

        followTerrainCheckBox.setSelected(measureTool.isFollowTerrain());
        org.openide.awt.Mnemonics.setLocalizedText(followTerrainCheckBox, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.followTerrainCheckBox.text")); // NOI18N
        followTerrainCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                followTerrainCheckBoxActionPerformed(evt);
            }
        });

        rubberbandCheckBox.setSelected(measureTool.getController().isUseRubberBand());
        org.openide.awt.Mnemonics.setLocalizedText(rubberbandCheckBox, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.rubberbandCheckBox.text")); // NOI18N
        rubberbandCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rubberbandCheckBoxActionPerformed(evt);
            }
        });

        tooltipsCheckBox.setSelected(measureTool.isShowAnnotation());
        org.openide.awt.Mnemonics.setLocalizedText(tooltipsCheckBox, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.tooltipsCheckBox.text")); // NOI18N
        tooltipsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tooltipsCheckBoxActionPerformed(evt);
            }
        });

        pointsCheckBox.setSelected(measureTool.isShowControlPoints());
        org.openide.awt.Mnemonics.setLocalizedText(pointsCheckBox, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.pointsCheckBox.text")); // NOI18N
        pointsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointsCheckBoxActionPerformed(evt);
            }
        });

        freeHandCheckBox.setSelected(measureTool.getController().isFreeHand());
        org.openide.awt.Mnemonics.setLocalizedText(freeHandCheckBox, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.freeHandCheckBox.text")); // NOI18N
        freeHandCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freeHandCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(followTerrainCheckBox))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pointsCheckBox))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tooltipsCheckBox))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(freeHandCheckBox))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rubberbandCheckBox))
                    .addGroup(settingsPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pathCombo, 0, 74, Short.MAX_VALUE)))
                .addContainerGap())
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addComponent(followTerrainCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pointsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tooltipsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(freeHandCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rubberbandCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pathCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.jPanel1.border.title"))); // NOI18N

        lineColorButton.setBackground(measureTool.getLineColor());
        org.openide.awt.Mnemonics.setLocalizedText(lineColorButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.lineColorButton.text")); // NOI18N
        lineColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lineColorButtonActionPerformed(evt);
            }
        });

        pointsColorButton.setBackground(measureTool.getControlPointsAttributes().getBackgroundColor());
        org.openide.awt.Mnemonics.setLocalizedText(pointsColorButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.pointsColorButton.text")); // NOI18N
        pointsColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointsColorButtonActionPerformed(evt);
            }
        });

        tooltipsColorButton.setBackground(measureTool.getAnnotationAttributes().getTextColor());
        org.openide.awt.Mnemonics.setLocalizedText(tooltipsColorButton, org.openide.util.NbBundle.getMessage(MeasurementsTopComponent.class, "MeasurementsTopComponent.tooltipsColorButton.text")); // NOI18N
        tooltipsColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tooltipsColorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tooltipsColorButton)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lineColorButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                        .addComponent(pointsColorButton, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lineColorButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pointsColorButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tooltipsColorButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                    .addComponent(metricsPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(settingsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(settingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(metricsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void shapeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapeComboActionPerformed
    String item = (String) ((JComboBox) evt.getSource()).getSelectedItem();
    if (item.equals("Line")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_LINE);
    } else if (item.equals("Path")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_PATH);
    } else if (item.equals("Polygon")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_POLYGON);
    } else if (item.equals("Circle")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_CIRCLE);
    } else if (item.equals("Ellipse")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_ELLIPSE);
    } else if (item.equals("Square")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_SQUARE);
    } else if (item.equals("Rectangle")) {
        measureTool.setMeasureShapeType(MeasureTool.SHAPE_QUAD);
    }
}//GEN-LAST:event_shapeComboActionPerformed

private void lineColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lineColorButtonActionPerformed
    Color c = JColorChooser.showDialog(wwm.getWorldWindow(), "Choose a color...", ((JButton) evt.getSource()).getBackground());
    if (c != null) {
        ((JButton) evt.getSource()).setBackground(c);
        measureTool.setLineColor(c);
        Color fill = new Color(c.getRed() / 255f * .5f, c.getGreen() / 255f * .5f, c.getBlue() / 255f * .5f, .5f);
        measureTool.setFillColor(fill);
    }
}//GEN-LAST:event_lineColorButtonActionPerformed

private void pointsColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointsColorButtonActionPerformed
    Color c = JColorChooser.showDialog(wwm.getWorldWindow(), "Choose a color...", ((JButton) evt.getSource()).getBackground());
    if (c != null) {
        ((JButton) evt.getSource()).setBackground(c);
        measureTool.getControlPointsAttributes().setBackgroundColor(c);
    }
}//GEN-LAST:event_pointsColorButtonActionPerformed

private void tooltipsColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tooltipsColorButtonActionPerformed
    Color c = JColorChooser.showDialog(wwm.getWorldWindow(), "Choose a color...", ((JButton) evt.getSource()).getBackground());
    if (c != null) {
        ((JButton) evt.getSource()).setBackground(c);
        measureTool.getAnnotationAttributes().setTextColor(c);
    }
}//GEN-LAST:event_tooltipsColorButtonActionPerformed

private void pathComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pathComboActionPerformed
    String item = (String) ((JComboBox) evt.getSource()).getSelectedItem();
    if (item.equals("Linear")) {
        measureTool.setPathType(AVKey.LINEAR);
    } else if (item.equals("Rhumb")) {
        measureTool.setPathType(AVKey.RHUMB_LINE);
    } else if (item.equals("Great circle")) {
        measureTool.setPathType(AVKey.GREAT_CIRCLE);
    }
}//GEN-LAST:event_pathComboActionPerformed

private void rubberbandCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rubberbandCheckBoxActionPerformed
    JCheckBox cb = (JCheckBox) evt.getSource();
    measureTool.getController().setUseRubberBand(cb.isSelected());
    freeHandCheckBox.setEnabled(cb.isSelected());
    wwm.getWorldWindow().redraw();
}//GEN-LAST:event_rubberbandCheckBoxActionPerformed

private void freeHandCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freeHandCheckBoxActionPerformed
    JCheckBox cb = (JCheckBox) evt.getSource();
    measureTool.getController().setFreeHand(cb.isSelected());
    wwm.getWorldWindow().redraw();
}//GEN-LAST:event_freeHandCheckBoxActionPerformed

private void followTerrainCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_followTerrainCheckBoxActionPerformed
    JCheckBox cb = (JCheckBox) evt.getSource();
    measureTool.setFollowTerrain(cb.isSelected());
    wwm.getWorldWindow().redraw();
}//GEN-LAST:event_followTerrainCheckBoxActionPerformed

private void pointsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointsCheckBoxActionPerformed
    JCheckBox cb = (JCheckBox) evt.getSource();
    measureTool.setShowControlPoints(cb.isSelected());
    wwm.getWorldWindow().redraw();
}//GEN-LAST:event_pointsCheckBoxActionPerformed

private void tooltipsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tooltipsCheckBoxActionPerformed
    JCheckBox cb = (JCheckBox) evt.getSource();
    measureTool.setShowAnnotation(cb.isSelected());
    wwm.getWorldWindow().redraw();
}//GEN-LAST:event_tooltipsCheckBoxActionPerformed

private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
    measureTool.clear();
    measureTool.setArmed(true);
}//GEN-LAST:event_startButtonActionPerformed

private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
    measureTool.setArmed(!measureTool.isArmed());
    pauseButton.setText(!measureTool.isArmed() ? "Resume" : "Pause");
    pauseButton.setEnabled(true);
    ((Component) wwm.getWorldWindow()).setCursor(!measureTool.isArmed() ? Cursor.getDefaultCursor()
            : Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
}//GEN-LAST:event_pauseButtonActionPerformed

private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
    measureTool.setArmed(false);
}//GEN-LAST:event_clearButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel areaLabel;
    private javax.swing.JLabel areaValueLabel;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel centerLabel;
    private javax.swing.JLabel centerValueLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JCheckBox followTerrainCheckBox;
    private javax.swing.JCheckBox freeHandCheckBox;
    private javax.swing.JLabel headingLabel;
    private javax.swing.JLabel headingValueLabel;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JLabel heightValueLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lengthLabel;
    private javax.swing.JLabel lengthValueLabel;
    private javax.swing.JButton lineColorButton;
    private javax.swing.JPanel metricsPanel;
    private javax.swing.JComboBox pathCombo;
    private javax.swing.JButton pauseButton;
    private javax.swing.JCheckBox pointsCheckBox;
    private javax.swing.JButton pointsColorButton;
    private javax.swing.JTextArea positionLog;
    private javax.swing.JCheckBox rubberbandCheckBox;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JComboBox shapeCombo;
    private javax.swing.JButton startButton;
    private javax.swing.JCheckBox tooltipsCheckBox;
    private javax.swing.JButton tooltipsColorButton;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JLabel widthValueLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        wwm.getLayers().addIfAbsent(measureTool.getLayer());
        wwm.getLayers().addIfAbsent(profile);
    }

    @Override
    public void componentClosed() {
        wwm.getLayers().remove(profile);
        wwm.getLayers().remove(measureTool.getLayer());
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
