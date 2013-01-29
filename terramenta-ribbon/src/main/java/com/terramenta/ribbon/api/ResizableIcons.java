/*
 * Copyright (c) 2010 Chris Böhme - Pinkmatter Solutions. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of Chris Böhme, Pinkmatter Solutions, nor the names of
 *    any contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.terramenta.ribbon.api;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

/**
 *
 * @author Chris
 */
public class ResizableIcons {

    public static final ResizableIcon EMPTY = new Empty();

    private ResizableIcons() {
    }

    public static ResizableIcon fromResource(String resource) {
        return new DiscreteResizableIcon(resource);
    }

    public static ResizableIcon fromImage(Image image) {
        return new ResizableImageIcon(image);
    }

    public static ResizableIcon binary(Icon smallIcon, Icon largeIcon) {
        return new BinaryResizableIcon(smallIcon, largeIcon);
    }

    public static ResizableIcon empty() {
        return EMPTY;
    }

    private static class BinaryResizableIcon implements ResizableIcon {

        private Icon small;
        private Icon large;
        private Icon delegate;
        private int width;
        private int height;

        public BinaryResizableIcon(Icon small, Icon large) {
            this.small = small;
            this.large = large;
            if (small == null) {
                small = large;
            }
            if (large == null) {
                large = small;
            }
            if (small != null) {
                setSize(small.getIconWidth(), small.getIconWidth());
            } else if (large != null) {
                setSize(large.getIconWidth(), large.getIconWidth());
            }
        }

        @Override
        public void setDimension(Dimension size) {
            setSize((int) size.getWidth(), (int) size.getHeight());
        }

        private void setSize(int width, int height) {
            if (small != null || large != null) {
                if (width != this.width || height != this.height) {
                    this.width = width;
                    this.height = height;
                    if (height > small.getIconHeight() + 2) {
                        delegate = large;
                    } else {
                        delegate = small;
                    }
                }
            }
        }

        @Override
        public int getIconHeight() {
            if (delegate != null) {
                return delegate.getIconHeight();
            } else {
                return height;
            }
        }

        @Override
        public int getIconWidth() {
            if (delegate != null) {
                return delegate.getIconWidth();
            } else {
                return width;
            }
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (delegate != null) {
                delegate.paintIcon(c, g, x, y);
            } else {
                g.setColor(Color.GRAY);
                g.fillRect(x, y, getIconWidth(), getIconHeight());
            }
        }
    }

    private static class ResizableImageIcon extends ImageIcon implements ResizableIcon {

        public ResizableImageIcon(Image image) {
            super(image);
        }

        @Override
        public void setDimension(Dimension dmnsn) {
        }
    }

    private static class Empty implements ResizableIcon {

        @Override
        public void setDimension(Dimension dmnsn) {
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
        }

        @Override
        public int getIconWidth() {
            return 0;
        }

        @Override
        public int getIconHeight() {
            return 0;
        }
    }
}
