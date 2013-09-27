package com.terramenta.globe.layers;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.AnnotationAttributes;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.ScreenAnnotation;
import gov.nasa.worldwind.util.Logging;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TimeControlsLayer extends RenderableLayer {

    protected final static String IMAGE_PLAY = "images/controlPlay.png";
    protected final static String IMAGE_REWIND = "images/controlRewind.png";
    protected final static String IMAGE_STOP = "images/controlStop.png";
    protected final static String IMAGE_STEPFORWARD = "images/controlStepForward.png";
    protected final static String IMAGE_STEPBACKWARD = "images/controlStepBackward.png";
    protected ScreenAnnotation controlPlay;
    protected ScreenAnnotation controlStop;
    protected ScreenAnnotation controlStepForward;
    protected ScreenAnnotation controlRewind;
    protected ScreenAnnotation controlStepBackward;
    protected ScreenAnnotation currentControl;
    protected String position = AVKey.SOUTH;
    protected String layout = AVKey.HORIZONTAL;
    protected Rectangle referenceViewPort;
    protected Vec4 locationCenter = null;
    protected Vec4 locationOffset = null;
    protected int borderWidth = 0;
    protected int buttonSize = 16;
    protected boolean initialized = false;
    protected PropertyChangeListener selectListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            select(evt.getSource(), evt.getNewValue().equals(Boolean.TRUE));
        }
    };
    protected PropertyChangeListener highlightListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            highlight(evt.getSource(), evt.getNewValue().equals(Boolean.TRUE));
        }
    };

    public int getBorderWidth() {
        return this.borderWidth;
    }

    /**
     * Sets the view controls offset from the viewport border.
     *
     * @param borderWidth the number of pixels to offset the view controls from the borders indicated by {@link
     *                    #setPosition(String)}.
     */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        clearControls();
    }

    protected int getButtonSize() {
        return buttonSize;
    }

    protected void setButtonSize(int buttonSize) {
        this.buttonSize = buttonSize;
        clearControls();
    }

    /**
     * Returns the current relative view controls position.
     *
     * @return the current view controls position.
     */
    public String getPosition() {
        return this.position;
    }

    /**
     * Sets the relative viewport location to display the view controls. Can be one of {@link AVKey#NORTHEAST}, {@link
     * AVKey#NORTHWEST}, {@link AVKey#SOUTHEAST}, or {@link AVKey#SOUTHWEST} (the default). These indicate the corner of
     * the viewport to place view controls.
     *
     * @param position the desired view controls position, in screen coordinates.
     */
    public void setPosition(String position) {
        if (position == null) {
            String message = Logging.getMessage("nullValue.PositionIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        this.position = position;
        clearControls();
    }

    /**
     * Returns the current layout. Can be one of {@link AVKey#HORIZONTAL} or {@link AVKey#VERTICAL}.
     *
     * @return the current layout.
     */
    public String getLayout() {
        return this.layout;
    }

    /**
     * Sets the desired layout. Can be one of {@link AVKey#HORIZONTAL} or {@link AVKey#VERTICAL}.
     *
     * @param layout the desired layout.
     */
    public void setLayout(String layout) {
        if (layout == null) {
            String message = Logging.getMessage("nullValue.StringIsNull");
            Logging.logger().severe(message);
            throw new IllegalArgumentException(message);
        }
        if (!this.layout.equals(layout)) {
            this.layout = layout;
            clearControls();
        }
    }

    /**
     * Layer opacity is not applied to layers of this type. Opacity is controlled by the alpha values of the operation
     * images.
     *
     * @param opacity the current opacity value, which is ignored by this layer.
     */
    @Override
    public void setOpacity(double opacity) {
        super.setOpacity(opacity);
    }

    /**
     * Returns the layer's opacity value, which is ignored by this layer. Opacity is controlled by the alpha values of
     * the operation images.
     *
     * @return The layer opacity, a value between 0 and 1.
     */
    @Override
    public double getOpacity() {
        return super.getOpacity();
    }

    /**
     * Returns the current layer image location.
     *
     * @return the current location center. May be null.
     */
    public Vec4 getLocationCenter() {
        return locationCenter;
    }

    /**
     * Specifies the screen location of the layer, relative to the image's center. May be null. If this value is
     * non-null, it overrides the position specified by {@link #setPosition(String)}. The location is specified in
     * pixels. The origin is the window's lower left corner. Positive X values are to the right of the origin, positive
     * Y values are upwards from the origin. The final image location will be affected by the currently specified
     * location offset if a non-null location offset has been specified (see {@link
     * #setLocationOffset(gov.nasa.worldwind.geom.Vec4)} )}.
     *
     * @param locationCenter the location center. May be null.
     * @see #setPosition(String)
     * @see #setLocationOffset(gov.nasa.worldwind.geom.Vec4)
     */
    public void setLocationCenter(Vec4 locationCenter) {
        this.locationCenter = locationCenter;
        clearControls();
    }

    /**
     * Returns the current location offset. See #setLocationOffset for a description of the offset and its values.
     *
     * @return the location offset. Will be null if no offset has been specified.
     */
    public Vec4 getLocationOffset() {
        return locationOffset;
    }

    /**
     * Specifies a placement offset from the layer position on the screen.
     *
     * @param locationOffset the number of pixels to shift the layer image from its specified screen position. A
     *                       positive X value shifts the image to the right. A positive Y value shifts the image up. If
     *                       null, no offset is applied. The default offset is null.
     * @see #setLocationCenter(gov.nasa.worldwind.geom.Vec4)
     * @see #setPosition(String)
     */
    public void setLocationOffset(Vec4 locationOffset) {
        this.locationOffset = locationOffset;
        clearControls();
    }

    /**
     * Indicates the currently highlighted control, if any.
     *
     * @return the currently highlighted control, or null if no control is highlighted.
     */
    public Object getHighlightedObject() {
        return this.currentControl;
    }

    /**
     * Specifies the control to highlight. Any currently highlighted control is un-highlighted.
     *
     * @param control the control to highlight.
     */
    public void highlight(Object control, boolean state) {
        // Manage highlighting of controls.
        if (state) {
            if (this.currentControl == control) {
                return; // same thing selected
            }

            // Turn on highlight if object selected.
            if (control != null && control instanceof ScreenAnnotation) {
                this.currentControl = (ScreenAnnotation) control;
                this.currentControl.getAttributes().setImageOpacity(1);
            }
        } else {
            // Turn off highlight if on.
            if (this.currentControl != null) {
                this.currentControl.getAttributes().setImageOpacity(-1); // use default opacity
                this.currentControl = null;
            }
        }
    }

    public void select(Object control, boolean state) {
        //TODO:
    }

    @Override
    public void doRender(DrawContext dc) {
        if (!this.initialized) {
            initialize(dc);
        }

        if (!this.referenceViewPort.equals(dc.getView().getViewport())) {
            updatePositions(dc);
        }

        super.doRender(dc);
    }

    protected boolean isInitialized() {
        return initialized;
    }

    protected void initialize(DrawContext dc) {
        if (this.initialized) {
            return;
        }

        // Setup user interface - common default attributes
        AnnotationAttributes ca = new AnnotationAttributes();
        ca.setAdjustWidthToText(AVKey.SIZE_FIXED);
        ca.setInsets(new Insets(0, 0, 0, 0));
        ca.setBorderWidth(0);
        ca.setCornerRadius(0);
        ca.setSize(new Dimension(buttonSize, buttonSize));
        ca.setBackgroundColor(new Color(0, 0, 0, 0));
        ca.setImageOpacity(.5);

        final String NOTEXT = "";
        final Point ORIGIN = new Point(0, 0);

        controlPlay = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
        controlPlay.getAttributes().setImageSource(IMAGE_PLAY);
        controlPlay.getAttributes().setSize(new Dimension(buttonSize, buttonSize));
        controlPlay.addPropertyChangeListener("SELECTED", selectListener);
        //controlPlay.addPropertyChangeListener("HOVER", highlightListener);
        //controlPlay.addPropertyChangeListener("ROLLOVER", highlightListener);
        this.addRenderable(controlPlay);

        controlStepBackward = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
        controlStepBackward.getAttributes().setImageSource(IMAGE_STEPBACKWARD);
        controlStepBackward.getAttributes().setSize(new Dimension(buttonSize, buttonSize));
        controlStepBackward.addPropertyChangeListener("SELECTED", selectListener);
        //controlStepBackward.addPropertyChangeListener("HOVER", highlightListener);
        //controlStepBackward.addPropertyChangeListener("ROLLOVER", highlightListener);
        this.addRenderable(controlStepBackward);

        controlStepForward = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
        controlStepForward.getAttributes().setImageSource(IMAGE_STEPFORWARD);
        controlStepForward.getAttributes().setSize(new Dimension(buttonSize, buttonSize));
        controlStepForward.addPropertyChangeListener("SELECTED", selectListener);
        //controlStepForward.addPropertyChangeListener("HOVER", highlightListener);
        //controlStepForward.addPropertyChangeListener("ROLLOVER", highlightListener);
        this.addRenderable(controlStepForward);

        controlRewind = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
        controlRewind.getAttributes().setImageSource(IMAGE_REWIND);
        controlRewind.getAttributes().setSize(new Dimension(buttonSize, buttonSize));
        controlRewind.addPropertyChangeListener("SELECTED", selectListener);
        //controlRewind.addPropertyChangeListener("HOVER", highlightListener);
        //controlRewind.addPropertyChangeListener("ROLLOVER", highlightListener);
        this.addRenderable(controlRewind);

        controlStop = new ScreenAnnotation(NOTEXT, ORIGIN, ca);
        controlStop.getAttributes().setImageSource(IMAGE_STOP);
        controlStop.getAttributes().setSize(new Dimension(buttonSize, buttonSize));
        controlStop.addPropertyChangeListener("SELECTED", selectListener);
        //controlStop.addPropertyChangeListener("HOVER", highlightListener);
        //controlStop.addPropertyChangeListener("ROLLOVER", highlightListener);
        this.addRenderable(controlStop);

        // Place controls according to layout and viewport dimension
        updatePositions(dc);

        this.initialized = true;
    }

    // Set controls positions according to layout and viewport dimension
    protected void updatePositions(DrawContext dc) {
        boolean horizontalLayout = this.layout.equals(AVKey.HORIZONTAL);

        int width = buttonSize * 5;
        int height = buttonSize;
        int xOffset = 0;
        int yOffset = buttonSize;

        if (!horizontalLayout) {
            // vertical layout
            int temp = height;
            height = width;
            width = temp;
            xOffset = buttonSize;
            yOffset = 0;
        }

        Rectangle controlsRectangle = new Rectangle(width, height);
        Point locationSW = computeLocation(dc.getView().getViewport(), controlsRectangle);

        // Layout start point
        int x = locationSW.x;
        int y = horizontalLayout ? locationSW.y : locationSW.y + height;


        if (!horizontalLayout) {
            y -= buttonSize;
        }
        controlStepBackward.setScreenPoint(new Point(x + xOffset, y + yOffset));
        if (horizontalLayout) {
            x += buttonSize;
        }

        if (!horizontalLayout) {
            y -= buttonSize;
        }
        controlRewind.setScreenPoint(new Point(x + xOffset, y + yOffset));
        if (horizontalLayout) {
            x += buttonSize;
        }

        if (!horizontalLayout) {
            y -= buttonSize;
        }
        controlStop.setScreenPoint(new Point(x + xOffset, y + yOffset));
        if (horizontalLayout) {
            x += buttonSize;
        }

        if (!horizontalLayout) {
            y -= buttonSize;
        }
        controlPlay.setScreenPoint(new Point(x + xOffset, y + yOffset));
        if (horizontalLayout) {
            x += buttonSize;
        }

        if (!horizontalLayout) {
            y -= buttonSize;
        }
        controlStepForward.setScreenPoint(new Point(x + xOffset, y + yOffset));
        if (horizontalLayout) {
            x += buttonSize;
        }

        this.referenceViewPort = dc.getView().getViewport();

    }

    /**
     * Compute the screen location of the controls overall rectangle bottom right corner according to either the
     * location center if not null, or the screen position.
     *
     * @param viewport the current viewport rectangle.
     * @param controls the overall controls rectangle
     * @return the screen location of the bottom left corner - south west corner.
     */
    protected Point computeLocation(Rectangle viewport, Rectangle controls) {
        double x;
        double y;

        if (this.locationCenter != null) {
            x = this.locationCenter.x - controls.width / 2;
            y = this.locationCenter.y - controls.height / 2;
        } else if (this.position.equals(AVKey.NORTH)) {
            x = viewport.getWidth() / 2 - controls.width / 2;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        } else if (this.position.equals(AVKey.NORTHEAST)) {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        } else if (this.position.equals(AVKey.SOUTH)) {
            x = viewport.getWidth() / 2 - controls.width / 2;
            y = 0d + this.borderWidth;
        } else if (this.position.equals(AVKey.SOUTHEAST)) {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = 0d + this.borderWidth;
        } else if (this.position.equals(AVKey.NORTHWEST)) {
            x = 0d + this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        } else if (this.position.equals(AVKey.SOUTHWEST)) {
            x = 0d + this.borderWidth;
            y = 0d + this.borderWidth;
        } else // use North East as default
        {
            x = viewport.getWidth() - controls.width - this.borderWidth;
            y = viewport.getHeight() - controls.height - this.borderWidth;
        }

        if (this.locationOffset != null) {
            x += this.locationOffset.x;
            y += this.locationOffset.y;
        }

        return new Point((int) x, (int) y);
    }

    protected void clearControls() {
        this.removeAllRenderables();
        this.controlPlay = null;
        this.controlStop = null;
        this.controlStepForward = null;
        this.controlRewind = null;
        this.controlStepBackward = null;
        this.initialized = false;
    }
}
