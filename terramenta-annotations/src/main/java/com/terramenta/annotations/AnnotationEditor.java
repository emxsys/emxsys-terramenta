/*
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
package com.terramenta.annotations;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.render.SurfaceShape;
import gov.nasa.worldwind.util.measure.MeasureTool;
import gov.nasa.worldwind.util.measure.MeasureToolController;
import org.openide.util.Lookup;

/**
 *
 * @author Chris.Heidt
 */
public class AnnotationEditor {

    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);
    //TODO: use a better tool than the MeasureTool
    private static final MeasureTool mt = new MeasureTool(wwm.getWorldWindow());
    private static final MeasureToolController mtc = new MeasureToolController();
    private static boolean isEditing = false;

    static {
        //does this seem stupid to anybody else?
        mtc.setMeasureTool(mt);
        mt.setController(mtc);

        mt.getLayer().setName("Annotation Editor");
    }

    public static void modify(SurfaceShape shape) {
        if (isEditing(shape)) {
            return;
        }
        isEditing = true;
        mt.setArmed(true);
        mt.setMeasureShape(shape);
        mt.getLayer().setEnabled(true);
    }

    public static void commit() {
        mt.setArmed(false);
        mt.getLayer().setEnabled(false);
        mt.getControlPoints().clear();

        isEditing = false;
    }

    public static void commit(SurfaceShape shape) {
        if (isEditing(shape)) {
            commit();
        }
    }

    public static boolean isEditing() {
        return isEditing;
    }

    public static boolean isEditing(SurfaceShape shape) {
        return (isEditing() && mt.getSurfaceShape().equals(shape));
    }
}
