/* 
 * Copyright (c) 2014, Bruce Schubert. <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the Emxsys company nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.terramenta.ribbon;

import java.awt.*;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicBandControlPanelUI;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;


/**
 * This class draws the bands (groups) of command buttons within a task.
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 * @version $Id: EmxsysBandControlPanelUI.java 100 2012-02-29 14:41:51Z bdschubert $
 */
public class Office2007BandControlPanelUI extends BasicBandControlPanelUI
{

    public static ComponentUI createUI(JComponent c)
    {
        return new Office2007BandControlPanelUI();
    }



    /**
     * Override to paint the band background with a gradient.
     *
     * @param graphics
     * @param toFill
     */
    @Override
    protected void paintBandBackground(Graphics graphics, Rectangle toFill)
    {
        //super.paintBandBackground(graphics, toFill);

        Graphics2D g2d = (Graphics2D) graphics.create();
        Color backgroundColor = controlPanel.getBackground();
        Paint paint = new GradientPaint(0, 0, FlamingoUtilities.getLighterColor(backgroundColor, 0.75),            
            0, toFill.height, backgroundColor);
        g2d.setPaint(paint);
        g2d.fillRect(toFill.x, toFill.y, toFill.width, toFill.height);
        g2d.dispose();

    }
}
