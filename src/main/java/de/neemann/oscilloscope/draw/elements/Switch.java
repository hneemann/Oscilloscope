package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.*;

import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE;
import static de.neemann.oscilloscope.draw.elements.Scaling.SIZE2;
import static de.neemann.oscilloscope.draw.graphics.Style.MAXLINETHICK;

/**
 * A simple switch.
 *
 * @param <T> the type of the elements
 */
public class Switch<T> extends ObservableElement<Switch<T>> {
    private final ArrayList<T> items;
    private final String name;
    private int selectedPosition;
    private Polygon polygon;

    /**
     * Creates a new switch
     *
     * @param name the name of the switch
     */
    public Switch(String name) {
        this.name = name;
        items = new ArrayList<>();
    }

    /**
     * Adds an item.
     *
     * @param item the item to add
     * @return this for chained calls
     */
    public Switch<T> add(T item) {
        items.add(item);
        return this;
    }

    /**
     * Adds a list of items
     *
     * @param items the items to add
     * @return this for chained calls
     */
    public Switch<T> add(T[] items) {
        for (T t : items)
            add(t);
        return this;
    }

    /**
     * Sets the switch to a certain value
     *
     * @param i the value
     * @return this for chained calls
     */
    public Switch<T> set(int i) {
        selectedPosition = i;
        hasChanged();
        return this;
    }

    /**
     * @return the selected value
     */
    public T getSelected() {
        return items.get(selectedPosition);
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        if (polygon == null)
            polygon = new Polygon(true)
                    .add(0, 0)
                    .add(SIZE, 0)
                    .add(SIZE, SIZE * items.size())
                    .add(0, SIZE * items.size());
        gr.drawPolygon(polygon, Style.NORMAL);

        gr.drawText(new Vector(SIZE2, -SIZE2), name, Orientation.CENTERBOTTOM, Style.PRINT);

        for (int i = 0; i < items.size(); i++)
            gr.drawText(new Vector(-4, i * SIZE + SIZE2), items.get(i).toString(), Orientation.RIGHTCENTER, Style.PRINT);

        int y = selectedPosition * SIZE;
        gr.drawPolygon(new Polygon(true)
                .add(MAXLINETHICK, y + MAXLINETHICK)
                .add(SIZE - MAXLINETHICK, y + MAXLINETHICK)
                .add(SIZE - MAXLINETHICK, y + SIZE - MAXLINETHICK)
                .add(MAXLINETHICK, y + SIZE - MAXLINETHICK), Style.SWITCH);
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
        if (items.size() == 2) {
            selectedPosition = 1 - selectedPosition;
            hasChanged();
        } else
            super.clicked(button, ctrl);
    }

    /**
     * Returns true if the switch matches the given value
     *
     * @param val the value
     * @return true if the switch matches the given value
     */
    public boolean is(T val) {
        return getSelected().equals(val);
    }
}
