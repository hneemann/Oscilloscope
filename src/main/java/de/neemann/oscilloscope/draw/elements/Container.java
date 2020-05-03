package de.neemann.oscilloscope.draw.elements;


import de.neemann.oscilloscope.draw.graphics.*;
import de.neemann.oscilloscope.draw.graphics.Polygon;

import java.awt.*;
import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * A Container tha holds other elements
 *
 * @param <T> the type of the container
 */
public class Container<T extends Container<?>> extends Element<T> {
    private static final int PAD = Switch.SIZE;

    private final String name;
    private final int dx;
    private final Polygon polygon;
    private final ArrayList<Element<?>> elements;
    private Style background;

    /**
     * Creates a new container
     */
    public Container() {
        this(null, 0, 0);
    }

    /**
     * Creates a new container
     *
     * @param dx the width
     * @param dy the height
     */
    public Container(int dx, int dy) {
        this(null, dx, dy);
    }

    /**
     * Creates a new container
     *
     * @param name the name used as a heading
     * @param dx   the width
     * @param dy   the height
     */
    public Container(String name, int dx, int dy) {
        this.name = name;
        this.dx = dx;
        this.elements = new ArrayList<>();
        if (dx > 0 && dy > 0)
            polygon = new Polygon(true)
                    .add(0, -PAD)
                    .add(dx, -PAD)
                    .add(new Vector(dx + PAD, -PAD), new Vector(dx + PAD, 0))
                    .add(dx + PAD, dy)
                    .add(new Vector(dx + PAD, dy + PAD), new Vector(dx, dy + PAD))
                    .add(0, dy + PAD)
                    .add(new Vector(-PAD, dy + PAD), new Vector(-PAD, dy))
                    .add(-PAD, 0)
                    .add(new Vector(-PAD, -PAD), new Vector(0, -PAD));
        else
            polygon = null;
    }

    /**
     * Sets the background color.
     * If not set the shape is transparent.
     *
     * @param background the color
     * @return this for chained calls
     */
    public Container<T> setBackground(Color background) {
        this.background = Style.NORMAL.deriveFillStyle(background);
        return this;
    }

    /**
     * Adds a element to the container
     *
     * @param e the element to add
     * @return this for chained calls
     */
    public Container<T> add(Element<?> e) {
        elements.add(e);
        return this;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        if (name != null && !name.isEmpty())
            gr.drawText(new Vector(dx / 2, -PAD - SIZE2 / 2), name, Orientation.CENTERBOTTOM, Style.PRINT);
        if (polygon != null) {
            if (background != null)
                gr.drawPolygon(polygon, background);

            gr.drawPolygon(polygon, Style.PRINT);
        }
        for (Element<?> e : elements)
            e.draw(gr);
    }

    /**
     * Returns the element at the given position
     *
     * @param pos the position
     * @return the element or null if no element was found
     */
    public Element<?> getElementAt(Vector pos) {
        Vector p = getTransform().invert().transform(pos);
        for (Element<?> e : elements) {
            if (e.getBoundingBox().match(p)) {
                if (e instanceof Container)
                    return ((Container<?>) e).getElementAt(p);
                else
                    return e;
            }
        }
        return null;
    }
}
