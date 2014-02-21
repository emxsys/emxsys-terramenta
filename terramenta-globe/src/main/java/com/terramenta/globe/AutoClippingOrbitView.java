/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.globe;

import gov.nasa.worldwind.view.orbit.BasicOrbitView;

/**
 *
 * @author Chris.Heidt
 */
public class AutoClippingOrbitView extends BasicOrbitView {

    @Override
    protected double computeNearClipDistance() {
        return super.computeNearClipDistance() / 4d;
    }

    @Override
    protected double computeFarClipDistance() {
        return super.computeFarClipDistance() * 4d;
    }
}
