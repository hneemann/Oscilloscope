package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * Abstraction of a potentiometer
 */
public class Potentiometer extends ObservableElement<Potentiometer> {
    private static final int MAX = 5000;
    private static final long SPEED = 50;

    private final int radius;
    private final String name;
    private int selectedPosition;
    private long time;
    private boolean centerZero;

    /**
     * Creates a new potentiometer
     *
     * @param name   the name
     * @param radius the size
     */
    public Potentiometer(String name, int radius) {
        this.name = name;
        this.radius = radius;
    }

    /**
     * @return the value of the poti in the range 0-1
     */
    public double get() {
        return ((double) selectedPosition) / MAX;
    }

    /**
     * Sets the potentiometers value
     *
     * @param v the new value
     * @return this for chained calls
     */
    public Potentiometer set(double v) {
        selectedPosition = (int) (v * MAX);
        hasChanged();
        return this;
    }

    /**
     * Sets the potentiometers value
     *
     * @return this for chained calls
     */
    public Potentiometer setCenterZero() {
        centerZero = true;
        return this;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        gr.drawCircle(new Vector(-radius, -radius), new Vector(radius, radius), Style.NORMAL);
        int r = radius - Style.MAXLINETHICK * 2;
        gr.drawCircle(new Vector(-r, -r), new Vector(r, r), Style.SWITCH);

        VectorInterface p1 = getOffset(0, radius + Style.MAXLINETHICK * 2);
        VectorInterface p2 = getOffset(0, radius + Style.MAXLINETHICK * 3);
        gr.drawLine(p1, p2, Style.PRINT);

        if (centerZero) {
            p1 = getOffset(MAX, radius + Style.MAXLINETHICK * 2);
            p2 = getOffset(MAX, radius + Style.MAXLINETHICK * 3);
            gr.drawLine(p1, p2, Style.PRINT);
        }

        VectorInterface p = getOffset(selectedPosition, r);
        gr.drawLine(new Vector(0, 0), p, Style.NORMAL);

        gr.drawText(new Vector(0, -radius - SIZE2), name, Orientation.CENTERBOTTOM, Style.PRINT);
    }

    private VectorInterface getOffset(int n, int radius) {
        double a = 3 * Math.PI / 2 * n / MAX + Math.PI * 3 / 4;
        return new VectorFloat((float) (radius * Math.cos(a)), (float) (radius * Math.sin(a)));
    }


    private int getDela(boolean ctrl) {
        long t = System.currentTimeMillis();
        long delta = 1;
        if (!ctrl) {
            delta = SPEED - (t - time);
            if (delta < 1)
                delta = 1;

            delta *= 10;
        }

        time = t;
        return (int) delta;
    }

    @Override
    public void down(boolean ctrl) {
        if (selectedPosition < MAX) {
            selectedPosition += getDela(ctrl);
            if (selectedPosition > MAX)
                selectedPosition = MAX;
            hasChanged();
        }
    }

    @Override
    public void up(boolean ctrl) {
        if (selectedPosition > 0) {
            selectedPosition -= getDela(ctrl);
            if (selectedPosition < 0)
                selectedPosition = 0;
            hasChanged();
        }
    }

}
