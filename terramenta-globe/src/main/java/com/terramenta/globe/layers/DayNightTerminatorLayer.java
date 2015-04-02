/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe.layers;

import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwindx.sunlight.Sun;
import gov.nasa.worldwindx.sunlight.SunDependent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chris Heidt <heidtmare@gmail.com>
 */
public class DayNightTerminatorLayer extends RenderableLayer implements Observer, SunDependent {

    private static final Logger logger = LoggerFactory.getLogger(DayNightTerminatorLayer.class);
    private static final Polyline terminator = new Polyline();
    private static final int numPoints = 4;
    private Sun sun;

    public DayNightTerminatorLayer() {
        terminator.setColor(Color.CYAN);
        terminator.setAntiAliasHint(Polyline.ANTIALIAS_DONT_CARE);
        terminator.setPathType(Polyline.GREAT_CIRCLE); // because this ends up being a big circle and this saves us on points needed
        terminator.setClosed(true);
        terminator.setLineWidth(2.0);
        terminator.setFollowTerrain(true);
        this.addRenderable(terminator);
    }

    public Sun getSun() {
        return sun;
    }

    @Override
    public void setSun(Sun sun) {
        if (this.sun != null) {
            this.sun.deleteObserver(this);
        }
        this.sun = sun;

        if (this.sun != null) {
            this.sun.addObserver(this);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Position sunPosition;
        if (arg instanceof Position) {
            sunPosition = (Position) arg;
        } else {
            sunPosition = sun.getPosition();
        }
        
        if(sunPosition == null){
            return;
        }

        List<LatLon> llVec = getFootPrintLatLonList(
                sunPosition.getLatitude().getDegrees(),
                sunPosition.getLongitude().getDegrees(),
                sunPosition.getAltitude(),
                numPoints);
        
        logger.info("Positions: {}",llVec);
        terminator.setPositions(llVec, 0.0);
    }

//    @Override
//    public void render(DrawContext dc) {
//        GL2 gl = dc.getGL().getGL2();
//        gl.glPushAttrib(GL2.GL_TEXTURE_BIT | GL2.GL_ENABLE_BIT | GL2.GL_CURRENT_BIT);
//
//        // Added so that the colors wouldn't depend on sun shading
//        gl.glDisable(GL.GL_TEXTURE_2D);
//
//        super.render(dc);
//
//        gl.glPopAttrib();
//    }
    // getFootPrintPolygons
    public static List<LatLon> getFootPrintLatLonList(double lat, double lon, double alt, int numPtsFootPrint) {

        // vars: ===============================
        //
        List<LatLon> llVec = new ArrayList<>(numPtsFootPrint);

        //========================================
        //disconnectCount = 0; // reset disconnect count
        double lambda0 = Math.acos(Earth.WGS84_EQUATORIAL_RADIUS / (Earth.WGS84_EQUATORIAL_RADIUS + alt));

        // TODO - convert first geodetic/geographic!!?
        double beta = (90 * Math.PI / 180.0 - lat); // latitude center (pitch)
        double gamma = -lon + 180.0 * Math.PI / 180.0; // longitude (yaw)

        // rotation matrix
        double[][] M = new double[][]{{Math.cos(beta) * Math.cos(gamma), Math.sin(gamma), -Math.sin(beta) * Math.cos(gamma)},
        {-Math.cos(beta) * Math.sin(gamma), Math.cos(gamma), Math.sin(beta) * Math.sin(gamma)},
        {Math.sin(beta), 0.0, Math.cos(beta)}};
        double theta = 0 + Math.PI / 2.0; // with extra offset of pi/2 so circle starts left of center going counter clockwise
        double phi = lambda0;

        // position
        double[] pos = new double[3];
        pos[0] = Earth.WGS84_EQUATORIAL_RADIUS * Math.cos(theta) * Math.sin(phi);
        pos[1] = Earth.WGS84_EQUATORIAL_RADIUS * Math.sin(theta) * Math.sin(phi);
        pos[2] = Earth.WGS84_EQUATORIAL_RADIUS * Math.cos(phi);

        // rotate to center around satellite sub point
        pos = mult(M, pos);

        // calculate Lat Long of point (first time save it)
        double[] llaOld = ecef2lla_Fast(pos);
        //llaOld[1] = llaOld[1] - 90.0*Math.PI/180.0;
        double[] lla = new double[3]; // prepare array

        // copy of orginal point
        double[] lla0 = new double[3];
        lla0[0] = llaOld[0];
        lla0[1] = llaOld[1];
        lla0[2] = llaOld[2];

        // add to vector
        llVec.add(LatLon.fromRadians(lla0[0], lla0[1]));

        // footprint parameters
        double dt = 2.0 * Math.PI / (numPtsFootPrint - 1.0);

        for (int j = 1; j < numPtsFootPrint; j++) {
            theta = j * dt + Math.PI / 2.0; // +Math.PI/2.0 // offset so it starts at the side
            //phi = lambda0;

            // find position - unrotated about north pole
            pos[0] = Earth.WGS84_POLAR_RADIUS * Math.cos(theta) * Math.sin(phi);
            pos[1] = Earth.WGS84_POLAR_RADIUS * Math.sin(theta) * Math.sin(phi);
            pos[2] = Earth.WGS84_POLAR_RADIUS * Math.cos(phi);

            // rotate to center around satellite sub point
            pos = mult(M, pos);

            // find lla
            lla = ecef2lla_Fast(pos);
            //lla[1] = lla[1]-90.0*Math.PI/180.0;
            //System.out.println("ll=" +lla[0]*180.0/Math.PI + "," + (lla[1]*180.0/Math.PI));

            // add to vector
            llVec.add(LatLon.fromRadians(lla[0], lla[1]));

        } // for each point around footprint

        // return
        return llVec;

    }

    // mult 3x3 matrices
    public static double[][] mult(double[][] a, double[][] b) {
        double[][] c = new double[3][3];

        for (int i = 0; i < 3; i++) // row
        {
            for (int j = 0; j < 3; j++) // col
            {
                c[i][j] = 0.0;
                for (int k = 0; k < 3; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return c;
    }

    // mult 3x3 matrices
    public static double[] mult(double[][] a, double[] b) {
        double[] c = new double[3];

        for (int i = 0; i < 3; i++) // row
        {
            c[i] = 0.0;
            for (int k = 0; k < 3; k++) {
                c[i] += a[i][k] * b[k];
            }
        }

        return c;

    }

    // function to convert earth-centered earth-fixed (ECEF) cartesian coordinates to Lat, Long, Alt
    // DOES NOT INCLUDE UPDATES FOR time
    // SEG 31 Match 2009 -- slightly less accurate (but faster) version of: GeoFunctions.calculateGeodeticLLA without time shift of latitude
    // source: http://www.mathworks.com/matlabcentral/fx_files/7941/1/ecef2lla.m
    // http://www.mathworks.com/matlabcentral/fileexchange/7941
    // for the reverse see: (which is the same as: GeoFunctions.lla2ecef
    // http://www.mathworks.com/matlabcentral/fileexchange/7942
    public static double[] ecef2lla_Fast(double[] pos) // d is current MDT time
    {
        double[] lla = new double[3];

        // WGS84 ellipsoid constants:
        double a = Earth.WGS84_EQUATORIAL_RADIUS;
        double e = 8.1819190842622e-2; // 0;%8.1819190842622e-2/a;%8.1819190842622e-2;  % 0.003352810664747

        double b = Math.sqrt(Math.pow(a, 2.0) * (1 - Math.pow(e, 2)));
        double ep = Math.sqrt((Math.pow(a, 2.0) - Math.pow(b, 2.0)) / Math.pow(b, 2.0));
        double p = Math.sqrt(Math.pow(pos[0], 2.0) + Math.pow(pos[1], 2.0));
        double th = Math.atan2(a * pos[2], b * p);
        lla[1] = Math.atan2(pos[1], pos[0]);
        lla[0] = Math.atan2((pos[2] + Math.pow(ep, 2.0) * b * Math.pow(Math.sin(th), 3.0)), (p - Math.pow(e, 2.0) * a * Math.pow(Math.cos(th), 3.0)));
        double N = a / Math.sqrt(1 - Math.pow(e, 2.0) * Math.pow(Math.sin(lla[0]), 2.0));
        lla[2] = p / Math.cos(lla[0]) - N;

        if (lla[1] < 0) {
            lla[1] = 2.0 * Math.PI + lla[1];
        }

        // return lon in range [0,2*pi)
        lla[1] = lla[1] % (2.0 * Math.PI); // modulus

        // correct for numerical instability in altitude near exact poles:
        // (after this correction, error is about 2 millimeters, which is about
        // the same as the numerical precision of the overall function)
        if (Math.abs(pos[0]) < 1.0 & Math.abs(pos[1]) < 1.0) {
            lla[2] = Math.abs(pos[2]) - b;
        }

        // now scale longitude from [0,360] -> [-180,180]
        if (lla[1] > Math.PI) // > 180
        {
            lla[1] = lla[1] - 2.0 * Math.PI;
        }
        /*
         * // now correct for time shift // account for earth rotations lla[1] = lla[1]-(280.4606
         * +360.9856473*d)*Math.PI/180.0; // correction ?? //lla[1] = lla[1]-Math.PI/2.0; // now
         * insure [-180,180] range double div = Math.floor(lla[1]/(2*Math.PI)); lla[1] = lla[1] -
         * div*2*Math.PI; if(lla[1] > Math.PI) { lla[1] = lla[1]- 2.0*Math.PI; }
         */

        return lla;

    } // ecef2lla
}
