package de.neemann.oscilloscope.draw.elements;


import de.neemann.oscilloscope.draw.graphics.*;

import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

public class Container<T extends Container<?>> extends Element<T> {
    private static final int PAD = Switch.SIZE;

    private final String name;
    private final int dx;
    private final Polygon polygon;
    private ArrayList<Element> elements;

    public Container(int dx, int dy) {
        this(null, dx, dy);
    }

    public Container(String name, int dx, int dy) {
        this.name = name;
        this.dx = dx;
        this.elements = new ArrayList<>();
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
    }

    public Container add(Element e) {
        elements.add(e);
        return this;
    }

    @Override
    void drawToOrigin(Graphic gr) {
        if (name != null && !name.isEmpty())
            gr.drawText(new Vector(dx / 2, -PAD - SIZE2 / 2), name, Orientation.CENTERBOTTOM, Style.PRINT);
        gr.drawPolygon(polygon, Style.PRINT);
        for (Element e : elements)
            e.draw(gr);
    }

    public Element getElementAt(Vector pos) {
        Vector p = getTransform().invert().transform(pos);
        for (Element e : elements) {
            if (e.getBoundingBox().match(p)) {
                if (e instanceof Container)
                    return ((Container) e).getElementAt(p);
                else
                    return e;
            }
        }
        return null;
    }
}
