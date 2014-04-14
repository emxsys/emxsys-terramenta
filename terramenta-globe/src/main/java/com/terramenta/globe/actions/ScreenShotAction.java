package com.terramenta.globe.actions;

import com.sun.opengl.util.Screenshot;
import com.terramenta.globe.WorldWindManager;
import com.terramenta.ribbon.RibbonActionReference;
import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLException;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Tools", id = "com.terramenta.globe.actions.ScreenShotAction")
@ActionRegistration(iconBase = "com/terramenta/globe/images/screenshot.png", displayName = "#CTL_ScreenShotAction", popupText = "Save an image of the current globe.")
@RibbonActionReference(path = "Menu/Tools/Create",
        position = 100,
        priority = "top",
        description = "#CTL_ScreenShotAction_Hint",
        tooltipTitle = "#CTL_ScreenShotAction_TooltipTitle",
        tooltipBody = "#CTL_ScreenShotAction_TooltipBody",
        tooltipIcon = "com/terramenta/globe/images/screenshot32.png",
        //tooltipFooter = "#CTL_Default_TooltipFooter",
        tooltipFooterIcon = "com/terramenta/images/help.png")
@Messages({
    "CTL_ScreenShotAction=Screen Shot",
    "CTL_ScreenShotAction_Hint=Save an image of the current globe.",
    "CTL_ScreenShotAction_TooltipTitle=Create Screen Shot",
    "CTL_ScreenShotAction_TooltipBody=Creates a screen shot of the current globe in the user's home directory.",})
/**
 * @author tag
 * @version $Id: ScreenShotAction.java 11809 2009-06-22 21:16:44Z tgaskins $
 *
 * Modified by R. Wathelet, click and shoot Modified by Travis Rennemann to prompt user with save
 * dialog (2014-03-10)
 */
public final class ScreenShotAction extends AbstractAction implements RenderingListener {

    private File snapFile = null;
    private static final WorldWindManager wwm = Lookup.getDefault().lookup(WorldWindManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {        
        snapFile = getFileFromUser((Component)e.getSource()); // prompt user to save the screenshot
        if (snapFile != null) {
            snapFile = getFileWithValidImgExt(snapFile); // make sure the user gave the file an ext. and that it's valid
        } else {
            return; // don't proceed because the user cancelled the save.
        }
        wwm.getWorldWindow().removeRenderingListener(this); // ensure not to add a duplicate
        wwm.getWorldWindow().addRenderingListener(this);
    }

    @Override
    public void stageChanged(RenderingEvent event) {
        if ((event.getStage().equals(RenderingEvent.AFTER_BUFFER_SWAP)) && (snapFile != null)) {
            try {
                GLAutoDrawable glad = (GLAutoDrawable) event.getSource();
                int[] viewport = new int[4];
                glad.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
                Screenshot.writeToFile(snapFile, viewport[2] + 10, viewport[3], false);
                glad.getGL().glViewport(0, 0, glad.getWidth(), glad.getHeight());
                System.out.printf("Image saved to file %s\n", snapFile.getCanonicalPath());
            } catch (IOException | GLException e) {
                System.err.println("Error encountered while saving screenshot: " + e.getMessage());
            } finally {
                wwm.getWorldWindow().removeRenderingListener(this);
            }
        }
    }

    /**
     * Prompt user to select where snapshot needs to be saved.
     *
     * @return
     */
    private File getFileFromUser(Component parent) {
        File userFile = null;
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Save Screen Shot");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG, JPG, GIF, BMP Images", "png", "jpg", "jpeg", "gif", "bmp");
        fc.setFileFilter(filter);

        int resultVal = fc.showSaveDialog(parent);
        if (resultVal == JFileChooser.APPROVE_OPTION) { // user selected a filename
            userFile = fc.getSelectedFile();
        }

        return userFile;
    }

    /**
     * Check that the user provided a file extension, if not then default to PNG.
     *
     * @param f The file to validate
     */
    private File getFileWithValidImgExt(File f) {
        String extension = getExtension(f);
        if (extension != null) {
            if (!extension.equals("png")
                    && !extension.equals("gif")
                    && !extension.equals("jpg")
                    && !extension.equals("jpeg")
                    && !extension.equals("bmp")) {
                // user entered an unknown file extension, set it to PNG
                return getFileWithDefaultImgExt(f);
            } else {
                return f;
            }
        } else {
            // user didn't provide a file extension, default to PNG
            return getFileWithDefaultImgExt(f);
        }
    }

    /**
     * Set file name extension to default for an image (PNG).
     *
     * @param f
     */
    private File getFileWithDefaultImgExt(File f) {
        File parent = f.getParentFile();
        String fileName = f.getName() + ".png";
        return new File(parent, fileName);
    }

    /**
     * Get the extension of a filename.
     *
     * @param f The file to check
     * @return
     */
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
