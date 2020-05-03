package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.*;

/**
 * Base class of all the gui elements.
 *
 * @param <T> the type of the implementing class
 */
public abstract class Element<T extends Element<?>> {
    private Transform transform = Transform.IDENTITY;
    private GraphicMinMax minMax;

    /**
     * Used to draw the element relative to the origin
     *
     * @param gr Graphic instance to draw to
     */
    abstract void drawToOrigin(Graphic gr);

    /**
     * Draws the component to the given Graphic instance
     *
     * @param gr Graphic instance to draw to
     */
    public final void draw(Graphic gr) {
        drawToOrigin(new GraphicTransform(gr, transform));
    }

    /**
     * Sets the transform
     *
     * @param transform the transform
     * @return this for chained calls
     */
    public T setTransform(Transform transform) {
        this.transform = transform;
        minMax = null;
        return (T) this;
    }

    /**
     * @return the transformation
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * Sets the position
     *
     * @param x x pos
     * @param y y pos
     * @return this for chained calls
     */
    public T setPos(int x, int y) {
        return setTransform(new TransformTranslate(x, y));
    }

    /**
     * @return the bounding box
     */
    public GraphicMinMax getBoundingBox() {
        if (minMax == null) {
            minMax = new GraphicMinMax(false);
            draw(minMax);
        }
        return minMax;
    }

    /**
     * Called if the mouse wheel is turned upwards.
     */
    public void up() {
    }

    /**
     * Called if the mouse wheel is turned downwards.
     */
    public void down() {
    }
}
