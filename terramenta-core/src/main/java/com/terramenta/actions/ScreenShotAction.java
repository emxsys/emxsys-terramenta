/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.actions;

import com.terramenta.globe.WorldWindManager;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools",
id = "com.terramenta.actions.ScreenShotAction")
@ActionRegistration(iconBase = "images/camera.png", displayName = "#CTL_ScreenShotAction")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 9),
    //@ActionReference(path = "Toolbars/Annotations", position = 9)
})
@Messages("CTL_ScreenShotAction=Screen Shot")
/**
 * @author tag
 * @version $Id: ScreenShotAction.java 11809 2009-06-22 21:16:44Z tgaskins $
 *
 * Modified by R. Wathelet, click and shoot
 */
public final class ScreenShotAction extends AbstractAction implements RenderingListener {

    private File snapFile;
    private String outputDir;
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    public ScreenShotAction() {
        outputDir = System.getProperty("user.home");
        if (outputDir == null) {
            outputDir = ".";
        }
    }

    // do not put the last slash here
    public ScreenShotAction(String dir) {
        outputDir = dir;
        if (outputDir == null) {
            outputDir = System.getProperty("user.home");
            if (outputDir == null) {
                outputDir = ".";
            }
        }
        if (outputDir.endsWith(System.getProperty("file.separator"))) {
            // remove the last slash
            outputDir = outputDir.substring(0, outputDir.length() - 1);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.composeSuggestedName();
        wwm.getWorldWindow().removeRenderingListener(this); // ensure not to add a duplicate
        wwm.getWorldWindow().addRenderingListener(this);
    }

    @Override
    public void stageChanged(RenderingEvent event) {
        if (event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP) && this.snapFile != null) {
            try {
                GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                int[] viewport = new int[4];
                glad.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                com.sun.opengl.util.Screenshot.writeToFile(this.snapFile, viewport[2] + 10, viewport[3], false);
                glad.getGL().glViewport(0, 0, glad.getWidth(), glad.getHeight());
                System.out.printf("Image saved to file %s\n", this.snapFile.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.snapFile = null;
                wwm.getWorldWindow().removeRenderingListener(this);
            }
        }
    }

    private String getDateTime() {
        //ISO 8601 date yyyy-MM-dd'T'HH:mm:ssz  does not work on windows xp
        return new SimpleDateFormat("yyMMddHHmmssZ").format(new Date());
    }

    private void composeSuggestedName() {
        String baseName = File.separator + "screenshot_" + getDateTime() + ".png";
        File snapShotFolder = new File(outputDir);
        try {
            if (!snapShotFolder.exists()) {
                snapShotFolder.mkdir();
            }
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        this.snapFile = new File(outputDir + baseName);
    }
}
