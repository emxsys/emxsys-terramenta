/*
Copyright (C) 2001, 2008 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
 */
package com.qna.terramenta.measurements;

import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.*;
import gov.nasa.worldwind.util.measure.MeasureTool;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**
 * Control panel for the MeasureTool.
 *
 * @author Patrick Murris
 * @version $Id: MeasureToolPanel.java 9992 2009-04-08 04:38:54Z dcollins $
 * @see gov.nasa.worldwind.util.measure.MeasureTool
 */
public class MeasurementPanel extends JPanel {

    private final WorldWindow wwd;
    private final MeasureTool measureTool;
    private JComboBox shapeCombo;
    private JComboBox pathTypeCombo;
    private JButton lineColorButton;
    private JButton pointColorButton;
    private JButton annotationColorButton;
    private JCheckBox followCheck;
    private JCheckBox showControlsCheck;
    private JCheckBox showAnnotationCheck;
    private JCheckBox rubberBandCheck;
    private JCheckBox freeHandCheck;
    private JButton newButton;
    private JButton pauseButton;
    private JButton endButton;
    private JLabel[] pointLabels;
    private JLabel lengthLabel;
    private JLabel areaLabel;
    private JLabel widthLabel;
    private JLabel heightLabel;
    private JLabel headingLabel;
    private JLabel centerLabel;
    private final Dimension size;

    /**
     *
     * @param wwdObject
     * @param measureToolObject
     */
    public MeasurementPanel(WorldWindow wwdObject, MeasureTool measureToolObject) {
        super(new BorderLayout());
        this.wwd = wwdObject;
        this.measureTool = measureToolObject;
        this.size = new Dimension(200, 300);
        this.initComponents();

        // Handle measure tool events
        measureTool.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                // Add, remove or change positions
                if (event.getPropertyName().equals(MeasureTool.EVENT_POSITION_ADD)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REMOVE)
                        || event.getPropertyName().equals(MeasureTool.EVENT_POSITION_REPLACE)) {
                    fillPointsPanel();    // Update position list when changed
                } // The tool was armed / disarmed
                else if (event.getPropertyName().equals(MeasureTool.EVENT_ARMED)) {
                    if (measureTool.isArmed()) {
                        newButton.setEnabled(false);
                        pauseButton.setText("Pause");
                        pauseButton.setEnabled(true);
                        endButton.setEnabled(true);
                        ((Component) wwd).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                    } else {
                        newButton.setEnabled(true);
                        pauseButton.setText("Pause");
                        pauseButton.setEnabled(false);
                        endButton.setEnabled(false);
                        ((Component) wwd).setCursor(Cursor.getDefaultCursor());
                    }
                } // Metric changed - sent after each render frame
                else if (event.getPropertyName().equals(MeasureTool.EVENT_METRIC_CHANGED)) {
                    updateMetric();
                }
            }
        });
    }

    /**
     *
     * @return
     */
    public MeasureTool getMeasureTool() {
        return this.measureTool;
    }

    /**
     *
     */
    private void initComponents() {
        // Shape combo
        JPanel shapePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        shapePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        shapePanel.add(new JLabel("Shape:"));
        shapeCombo = new JComboBox(new String[]{"Line", "Path", "Polygon", "Circle", "Ellipse", "Square", "Rectangle"});
        shapeCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                String item = (String) ((JComboBox) event.getSource()).getSelectedItem();
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
            }
        });
        shapePanel.add(shapeCombo);

        // Path type combo
        JPanel pathTypePanel = new JPanel(new GridLayout(1, 2, 5, 5));
        pathTypePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        pathTypePanel.add(new JLabel("Path type:"));
        pathTypeCombo = new JComboBox(new String[]{"Linear", "Rhumb", "Great circle"});
        pathTypeCombo.setSelectedIndex(2);
        pathTypeCombo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                String item = (String) ((JComboBox) event.getSource()).getSelectedItem();
                if (item.equals("Linear")) {
                    measureTool.setPathType(AVKey.LINEAR);
                } else if (item.equals("Rhumb")) {
                    measureTool.setPathType(AVKey.RHUMB_LINE);
                } else if (item.equals("Great circle")) {
                    measureTool.setPathType(AVKey.GREAT_CIRCLE);
                }
            }
        });
        pathTypePanel.add(pathTypeCombo);

        // Check boxes panel
        JPanel checkPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        checkPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        followCheck = new JCheckBox("Follow terrain");
        followCheck.setSelected(measureTool.isFollowTerrain());
        followCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                measureTool.setFollowTerrain(cb.isSelected());
                wwd.redraw();
            }
        });
        checkPanel.add(followCheck);

        showControlsCheck = new JCheckBox("Control points");
        showControlsCheck.setSelected(measureTool.isShowControlPoints());
        showControlsCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                measureTool.setShowControlPoints(cb.isSelected());
                wwd.redraw();
            }
        });
        checkPanel.add(showControlsCheck);

        rubberBandCheck = new JCheckBox("Rubber band");
        rubberBandCheck.setSelected(measureTool.getController().isUseRubberBand());
        rubberBandCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                measureTool.getController().setUseRubberBand(cb.isSelected());
                freeHandCheck.setEnabled(cb.isSelected());
                wwd.redraw();
            }
        });
        checkPanel.add(rubberBandCheck);

        freeHandCheck = new JCheckBox("Free Hand");
        freeHandCheck.setSelected(measureTool.getController().isFreeHand());
        freeHandCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                measureTool.getController().setFreeHand(cb.isSelected());
                wwd.redraw();
            }
        });
        checkPanel.add(freeHandCheck);

        showAnnotationCheck = new JCheckBox("Tooltip");
        showAnnotationCheck.setSelected(measureTool.isShowAnnotation());
        showAnnotationCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                JCheckBox cb = (JCheckBox) event.getSource();
                measureTool.setShowAnnotation(cb.isSelected());
                wwd.redraw();
            }
        });
        checkPanel.add(showAnnotationCheck);

        // Color buttons
        final JPanel colorPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        colorPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        lineColorButton = new JButton("Line");
        lineColorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                Color c = JColorChooser.showDialog(colorPanel,
                        "Choose a color...", ((JButton) event.getSource()).getBackground());
                if (c != null) {
                    ((JButton) event.getSource()).setBackground(c);
                    measureTool.setLineColor(c);
                    Color fill = new Color(c.getRed() / 255f * .5f,
                            c.getGreen() / 255f * .5f, c.getBlue() / 255f * .5f, .5f);
                    measureTool.setFillColor(fill);
                }
            }
        });
        colorPanel.add(lineColorButton);
        lineColorButton.setBackground(measureTool.getLineColor());

        pointColorButton = new JButton("Points");
        pointColorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                Color c = JColorChooser.showDialog(colorPanel,
                        "Choose a color...", ((JButton) event.getSource()).getBackground());
                if (c != null) {
                    ((JButton) event.getSource()).setBackground(c);
                    measureTool.getControlPointsAttributes().setBackgroundColor(c);
                }
            }
        });
        colorPanel.add(pointColorButton);
        pointColorButton.setBackground(measureTool.getControlPointsAttributes().getBackgroundColor());

        annotationColorButton = new JButton("Tooltip");
        annotationColorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                Color c = JColorChooser.showDialog(colorPanel,
                        "Choose a color...", ((JButton) event.getSource()).getBackground());
                if (c != null) {
                    ((JButton) event.getSource()).setBackground(c);
                    measureTool.getAnnotationAttributes().setTextColor(c);
                }
            }
        });
        annotationColorButton.setBackground(measureTool.getAnnotationAttributes().getTextColor());
        colorPanel.add(annotationColorButton);

        // Action buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                measureTool.clear();
                measureTool.setArmed(true);
            }
        });
        buttonPanel.add(newButton);
        newButton.setEnabled(true);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                measureTool.setArmed(!measureTool.isArmed());
                pauseButton.setText(!measureTool.isArmed() ? "Resume" : "Pause");
                pauseButton.setEnabled(true);
                ((Component) wwd).setCursor(!measureTool.isArmed() ? Cursor.getDefaultCursor()
                        : Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        });
        buttonPanel.add(pauseButton);
        pauseButton.setEnabled(false);

        endButton = new JButton("End");
        endButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                measureTool.setArmed(false);
            }
        });
        buttonPanel.add(endButton);
        endButton.setEnabled(false);

        // Point list
        JPanel pointPanel = new JPanel(new GridLayout(0, 1, 0, 4));
        pointPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        this.pointLabels = new JLabel[100];
        for (int i = 0; i < this.pointLabels.length; i++) {
            this.pointLabels[i] = new JLabel("");
            pointPanel.add(this.pointLabels[i]);
        }

        // Put the point panel in a container to prevent scroll panel from stretching the vertical spacing.
        JPanel dummyPanel = new JPanel(new BorderLayout());
        dummyPanel.add(pointPanel, BorderLayout.NORTH);

        // Put the point panel in a scroll bar.
        JScrollPane scrollPane = new JScrollPane(dummyPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        if (size != null) {
            scrollPane.setPreferredSize(size);
        }

        // Metric
        JPanel metricPanel = new JPanel(new GridLayout(0, 2, 0, 4));
        metricPanel.setBorder(new CompoundBorder(
                new TitledBorder("Metric"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        metricPanel.add(new JLabel("Length:"));
        lengthLabel = new JLabel();
        metricPanel.add(lengthLabel);
        metricPanel.add(new JLabel("Area:"));
        areaLabel = new JLabel();
        metricPanel.add(areaLabel);
        metricPanel.add(new JLabel("Width:"));
        widthLabel = new JLabel();
        metricPanel.add(widthLabel);
        metricPanel.add(new JLabel("Height:"));
        heightLabel = new JLabel();
        metricPanel.add(heightLabel);
        metricPanel.add(new JLabel("Heading:"));
        headingLabel = new JLabel();
        metricPanel.add(headingLabel);
        metricPanel.add(new JLabel("Center:"));
        centerLabel = new JLabel();
        metricPanel.add(centerLabel);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(colorPanel);
        this.add(shapePanel);
        this.add(pathTypePanel);
        this.add(checkPanel);
        this.add(buttonPanel);
        this.add(metricPanel);
        this.add(scrollPane);
    }

    /**
     *
     */
    private void fillPointsPanel() {
        int i = 0;
        if (measureTool.getPositions() != null) {
            for (LatLon pos : measureTool.getPositions()) {
                if (i == this.pointLabels.length) {
                    break;
                }

                String las = String.format("Lat %7.4f\u00B0", pos.getLatitude().getDegrees());
                String los = String.format("Lon %7.4f\u00B0", pos.getLongitude().getDegrees());
                pointLabels[i++].setText(las + "  " + los);
            }
        }
        // Clear remaining labels
        for (; i < this.pointLabels.length; i++) {
            pointLabels[i].setText("");
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
        lengthLabel.setText(s);

        // Update area label
        value = measureTool.getArea();
        if (value < 0) {
            s = "na";
        } else if (value < 1e6) {
            s = String.format("%,7.1f m2", value);
        } else {
            s = String.format("%,7.3f km2", value / 1e6);
        }
        areaLabel.setText(s);

        // Update width label
        value = measureTool.getWidth();
        if (value < 0) {
            s = "na";
        } else if (value < 1000) {
            s = String.format("%,7.1f m", value);
        } else {
            s = String.format("%,7.3f km", value / 1000);
        }
        widthLabel.setText(s);

        // Update height label
        value = measureTool.getHeight();
        if (value < 0) {
            s = "na";
        } else if (value < 1000) {
            s = String.format("%,7.1f m", value);
        } else {
            s = String.format("%,7.3f km", value / 1000);
        }
        heightLabel.setText(s);

        // Update heading label
        Angle angle = measureTool.getOrientation();
        if (angle != null) {
            s = String.format("%,6.2f\u00B0", angle.degrees);
        } else {
            s = "na";
        }
        headingLabel.setText(s);

        // Update center label
        Position center = measureTool.getCenterPosition();
        if (center != null) {
            s = String.format("%,7.4f\u00B0 %,7.4f\u00B0", center.getLatitude().degrees, center.getLongitude().degrees);
        } else {
            s = "na";
        }
        centerLabel.setText(s);
    }
}
