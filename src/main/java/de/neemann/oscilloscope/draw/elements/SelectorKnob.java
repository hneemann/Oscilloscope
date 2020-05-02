package de.neemann.oscilloscope.draw.elements;


import de.neemann.oscilloscope.draw.graphics.*;

import java.util.ArrayList;
import java.util.Collection;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

public class SelectorKnob<T> extends Element<SelectorKnob<T>> {
    private final int radius;
    private final ArrayList<T> items;
    private final String name;
    private int selectedPosition;

    public SelectorKnob(String name, int radius) {
        this.name = name;
        items = new ArrayList<>();
        this.radius = radius;
    }

    public SelectorKnob<T> add(T item) {
        items.add(item);
        return this;
    }

    public SelectorKnob<T> addAll(Collection<T> items) {
        this.items.addAll(items);
        return this;
    }

    public T getSelected() {
        return items.get(selectedPosition);
    }

    @Override
    void drawToOrigin(Graphic gr) {
        gr.drawCircle(new Vector(-radius, -radius), new Vector(radius, radius), Style.NORMAL);
        int r = radius - Style.MAXLINETHICK * 2;
        gr.drawCircle(new Vector(-r, -r), new Vector(r, r), Style.SWITCH);

        for (int i = 0; i < items.size(); i++) {
            VectorInterface p1 = getOffset(i, radius + Style.MAXLINETHICK * 2);
            VectorInterface p2 = getOffset(i, radius + Style.MAXLINETHICK * 3);
            gr.drawLine(p1, p2, Style.PRINT);

            VectorInterface p3 = getOffset(i, radius + Style.MAXLINETHICK * 4);
            gr.drawText(p3, items.get(i).toString(), Orientation.from(p3), Style.PRINT);
        }

        VectorInterface p1 = getOffset(selectedPosition, r);
        VectorInterface p2 = getOffset(selectedPosition, r / 3);
        gr.drawLine(p1, p2, Style.NORMAL);

        gr.drawText(new Vector(0, -radius - Style.MAXLINETHICK * 6 - SIZE2), name, Orientation.CENTERBOTTOM, Style.PRINT);
    }

    private VectorInterface getOffset(int n, int radius) {
        double a = 3 * Math.PI / 2 * n / (items.size() - 1) + Math.PI * 3 / 4;
        return new VectorFloat((float) (radius * Math.cos(a)), (float) (radius * Math.sin(a)));
    }

    @Override
    public void down() {
        if (selectedPosition < items.size() - 1)
            selectedPosition++;
    }

    @Override
    public void up() {
        if (selectedPosition > 0)
            selectedPosition--;
    }

}
