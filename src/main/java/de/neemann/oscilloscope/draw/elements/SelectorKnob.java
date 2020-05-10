package de.neemann.oscilloscope.draw.elements;


import de.neemann.oscilloscope.draw.graphics.*;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * Abstraction of a selector knob
 *
 * @param <T> type of the items
 */
public class SelectorKnob<T> extends ObservableElement<SelectorKnob<T>> {
    private final int radius;
    private final ArrayList<T> items;
    private final String name;
    private int selectedPosition;

    /**
     * Creates a new selector knob
     *
     * @param name   the name
     * @param radius the size
     */
    public SelectorKnob(String name, int radius) {
        this.name = name;
        items = new ArrayList<>();
        this.radius = radius;
    }

    /**
     * Adds a item to the knob
     *
     * @param item the list of items
     * @return this for chained calls
     */
    public SelectorKnob<T> add(T item) {
        items.add(item);
        return this;
    }

    /**
     * Adds all items to the knob
     *
     * @param items the list of items
     * @return this for chained calls
     */
    public SelectorKnob<T> addAll(Collection<T> items) {
        this.items.addAll(items);
        return this;
    }

    /**
     * Adds all items to the knob
     *
     * @param items the list of items
     * @return this for chained calls
     */
    public SelectorKnob<T> addAll(T[] items) {
        return addAll(Arrays.asList(items));
    }

    /**
     * @return the selected setting
     */
    public T getSelected() {
        return items.get(selectedPosition);
    }

    /**
     * @return the items
     */
    public ArrayList<T> getItems() {
        return items;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        gr.drawCircle(new Vector(-radius, -radius), new Vector(radius, radius), Style.NORMAL);
        int r = radius - Style.MAXLINETHICK * 2;
        gr.drawCircle(new Vector(-r, -r), new Vector(r, r), Style.SWITCH);

        for (int i = 0; i < items.size(); i++) {
            VectorInterface p1 = getOffset(i, radius + Style.MAXLINETHICK * 2);
            VectorInterface p2 = getOffset(i, radius + Style.MAXLINETHICK * 3);
            gr.drawLine(p1, p2, Style.PRINT);

            VectorInterface p3 = getOffset(i, radius + Style.MAXLINETHICK * 4);
            gr.drawText(p3, getStringFor(items.get(i)), Orientation.from(p3), Style.PRINT);
        }

        VectorInterface p1 = getOffset(selectedPosition, r);
        VectorInterface p2 = getOffset(selectedPosition, r / 3);
        gr.drawLine(p1, p2, Style.NORMAL);

        gr.drawText(new Vector(0, -radius - Style.MAXLINETHICK * 6 - SIZE2 - getNameOfs()), name, Orientation.CENTERBOTTOM, Style.PRINT);
    }

    int getNameOfs() {
        return 0;
    }

    String getStringFor(T t) {
        return t.toString();
    }

    VectorInterface getOffset(float n, int radius) {
        double a = 3 * Math.PI / 2 * n / (items.size() - 1) + Math.PI * 3 / 4;
        return new VectorFloat((float) (radius * Math.cos(a)), (float) (radius * Math.sin(a)));
    }

    @Override
    public void down(boolean ctrl) {
        if (selectedPosition < items.size() - 1) {
            selectedPosition++;
            hasChanged();
        }
    }

    @Override
    public void up(boolean ctrl) {
        if (selectedPosition > 0) {
            selectedPosition--;
            hasChanged();
        }
    }

    @Override
    public void clicked(int button, boolean ctrl) {
        if (button == MouseEvent.BUTTON1)
            up(ctrl);
        else if (button == MouseEvent.BUTTON3)
            down(ctrl);
    }

}
