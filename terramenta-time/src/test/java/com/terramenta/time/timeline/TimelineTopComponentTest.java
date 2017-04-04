/**
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
package com.terramenta.time.timeline;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author Chris Heidt <chris.heidt@vencore.com>
 */
public class TimelineTopComponentTest {
    
    public static void main(String[] args) throws IOException {
        TimelineTopComponent tc = new TimelineTopComponent();
        
        JFrame frame = new JFrame();
        frame.add(tc);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1000, 200);
        frame.show();
    }
    
}
