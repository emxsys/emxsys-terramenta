/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

    static {
        //does this seem stupid to anybody else?
        mtc.setMeasureTool(mt);
        mt.setController(mtc);
    }

    public static void newAnnotation(SurfaceShape shape) {
        mt.clear();
        mt.setMeasureShape(shape);
        mt.setArmed(true);
    }

    public static void enableEdit(SurfaceShape shape) {
        if (isEditing(shape)) {
            return;
        }

        mt.setArmed(true);
        mt.setMeasureShape(shape);
    }

    public static void disableEdit(SurfaceShape shape) {
        if (!isEditing(shape)) {
            return;
        }

        mt.setArmed(false);
    }

    public static boolean isEditing(SurfaceShape shape) {
        return (mt.isArmed() && mt.getSurfaceShape().equals(shape));
    }
}
