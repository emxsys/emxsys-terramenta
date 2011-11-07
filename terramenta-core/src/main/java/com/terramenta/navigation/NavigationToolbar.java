/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NavigationToolbar.java
 *
 * Created on Sep 12, 2011, 1:44:57 PM
 */
package com.terramenta.navigation;

import com.terramenta.globe.WorldWindManager;
import com.terramenta.utilities.CoordinateSystem;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import gov.nasa.worldwind.geom.coords.UTMCoord;
import gov.nasa.worldwind.globes.Globe;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author heidtmare
 */
public class NavigationToolbar {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    private static final JTextField positionField = new JTextField();
    private static final JComboBox coordTypeComboBox = new JComboBox(CoordinateSystem.values());

    private static void parsePositionField() {
        Position position = null;
        CoordinateSystem system = (CoordinateSystem) coordTypeComboBox.getSelectedItem();
        if (system.equals(CoordinateSystem.LatLon)) {
            position = parseStringLatLon(positionField.getText());
        } else if (system.equals(CoordinateSystem.MGRS)) {
            position = parseStringMGRS(positionField.getText(), wwm.getWorldWindow().getModel().getGlobe());
        } else if (system.equals(CoordinateSystem.UTM)) {
            position = parseStringUTM(positionField.getText(), wwm.getWorldWindow().getModel().getGlobe());
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
        } catch (Exception e) {
            //Exceptions.printStackTrace(e);
        }
        return position;
    }

    private static Position parseStringMGRS(String text, Globe globe) {
        Position position = null;
        try {
            MGRSCoord mgrs = MGRSCoord.fromString(text, globe);
            position = new Position(mgrs.getLatitude(), mgrs.getLongitude(), 0);
        } catch (Exception e) {
            //Exceptions.printStackTrace(e);
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
        } catch (Exception e) {
            //Exceptions.printStackTrace(e);
        }
        return position;
    }

    @ActionID(category = "Tools", id = "com.terramenta.navigation.NavigationCoordType")
    @ActionRegistration(displayName = "#CTL_NavigationCoordType")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Navigation", position = 1)
    })
    @Messages("CTL_NavigationCoordType=Select Coord Type")
    public static final class NavigationCoordType extends AbstractAction implements Presenter.Toolbar {

        public NavigationCoordType() {
            coordTypeComboBox.setMaximumSize(new Dimension(70, 22));
            coordTypeComboBox.setMinimumSize(new Dimension(60, 22));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //...
        }

        @Override
        public Component getToolbarPresenter() {
            return coordTypeComboBox;
        }
    }

    @ActionID(category = "Tools", id = "com.terramenta.navigation.NavigationSpacer")
    @ActionRegistration(displayName = "#CTL_NavigationSpacer")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Navigation", position = 2)
    })
    @Messages("CTL_NavigationSpacer=")
    public static final class NavigationSpacer extends AbstractAction implements Presenter.Toolbar {

        @Override
        public void actionPerformed(ActionEvent e) {
            //...
        }

        @Override
        public Component getToolbarPresenter() {
            Dimension dim = new Dimension(2, 0);
            return new Box.Filler(dim, dim, dim);
        }
    }

    @ActionID(category = "Tools", id = "com.terramenta.navigation.NavigationCoordField")
    @ActionRegistration(displayName = "#CTL_NavigationCoordField")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Navigation", position = 3)
    })
    @Messages("CTL_NavigationCoordField=Type Position")
    public static final class NavigationCoordField extends AbstractAction implements Presenter.Toolbar {

        public NavigationCoordField() {
            positionField.setMaximumSize(new Dimension(150, 22));
            positionField.setPreferredSize(new Dimension(125, 22));
            positionField.setMinimumSize(new Dimension(100, 22));
            positionField.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            parsePositionField();
        }

        @Override
        public Component getToolbarPresenter() {
            return positionField;
        }
    }

    @ActionID(category = "Tools", id = "com.terramenta.navigation.NavigationGoButton")
    @ActionRegistration(iconBase = "images/bulletGo.png", displayName = "#CTL_NavigationGoButton")
    @ActionReferences({
        @ActionReference(path = "Toolbars/Navigation", position = 4)
    })
    @Messages("CTL_NavigationGoButton=Go to position...")
    public static final class NavigationGoButton extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            parsePositionField();
        }
    }
}
