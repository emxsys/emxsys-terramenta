/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.ribbons;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.globe.utilities.CoordinateSystem;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.geom.coords.UTMCoord;
import gov.nasa.worldwind.globes.Globe;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.pushingpixels.flamingo.api.ribbon.JFlowRibbonBand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author chris.heidt
 */
public class NavigationBand extends JFlowRibbonBand implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(NavigationBand.class);
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private final JTextField positionField;
    private final JComboBox coordTypeComboBox;

    public NavigationBand() {
        super("Navigation", null);
        setPreferredSize(new Dimension(250, 60));

        JPanel panel = new JPanel();

        coordTypeComboBox = new JComboBox(CoordinateSystem.values());
        coordTypeComboBox.setPreferredSize(new Dimension(65, 24));
        panel.add(coordTypeComboBox);

        positionField = new JTextField();
        positionField.setPreferredSize(new Dimension(150, 24));
        positionField.addActionListener(this);
        panel.add(positionField);

        JButton submitBtn = new JButton(ImageUtilities.loadImageIcon("com/terramenta/globe/images/bulletGo.png", false));
        submitBtn.setPreferredSize(new Dimension(24, 24));
        submitBtn.addActionListener(this);
        panel.add(submitBtn);

        this.addFlowComponent(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        parsePositionField((CoordinateSystem) coordTypeComboBox.getSelectedItem(), positionField.getText());
    }

    private static void parsePositionField(CoordinateSystem system, String text) {
        Position position = null;

        switch (system) {
            case LatLon:
                position = parseStringLatLon(text);
                break;
            case MGRS:
                position = parseStringMGRS(text, wwm.getWorldWindow().getModel().getGlobe());
                break;
            case UTM:
                position = parseStringUTM(text, wwm.getWorldWindow().getModel().getGlobe());
                break;
        }

        if (position != null) {
            wwm.gotoPosition(position);
        }
    }

    private static Position parseStringLatLon(String text) {
        Position position = null;
        try {
            String[] split = text.split(",");
            double lat = Double.parseDouble(split[0]);
            double lon = Double.parseDouble(split[1]);
            position = Position.fromDegrees(lat, lon);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse LatLon", e.getMessage());
        }
        return position;
    }

    private static Position parseStringMGRS(String text, Globe globe) {
        Position position = null;
        try {
            MGRSCoord mgrs = MGRSCoord.fromString(text, globe);
            position = new Position(mgrs.getLatitude(), mgrs.getLongitude(), 0);
        } catch (Exception e) {
            logger.warn("Failed to parse MGRS", e.getMessage());
        }
        return position;
    }

    private static Position parseStringUTM(String text, Globe globe) {
        Position position = null;
        try {
            String[] utmString = text.split(" ");

            int zone;
            String latZone;
            double easting;
            double northing;
            if (utmString[0].length() == 3) {
                //example 30U 0614769 5852359
                zone = Integer.parseInt(utmString[0].substring(0, 2));
                latZone = utmString[0].substring(2);
                easting = Double.parseDouble(utmString[1]);
                northing = Double.parseDouble(utmString[2]);
            } else {
                //example 30 U 0614769 5852359
                zone = Integer.parseInt(utmString[0]);
                latZone = utmString[1];
                easting = Double.parseDouble(utmString[2]);
                northing = Double.parseDouble(utmString[3]);
            }

            String hemisphere = AVKey.NORTH;
            if ("ACDEFGHJKLM".contains(latZone)) {
                hemisphere = AVKey.SOUTH;
            }
            UTMCoord utm = UTMCoord.fromUTM(zone, hemisphere, easting, northing, globe);
            position = new Position(utm.getLatitude(), utm.getLongitude(), 0);
        } catch (NumberFormatException e) {
            logger.warn("Failed to parse UTM", e.getMessage());
        }
        return position;
    }
}
