package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.draw.graphics.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * Abstraction of a input
 */
public class Input extends Element<Input> {
    private static final int RAD = SIZE + SIZE2;
    private static final int PIN = Style.MAXLINETHICK;
    private final String name;

    /**
     * Creates an input
     *
     * @param name the name of the input
     */
    public Input(String name) {
        this.name = name;
    }

    @Override
    void drawToOrigin(Graphic gr) {
        gr.drawCircle(new Vector(-RAD, -RAD), new Vector(RAD, RAD), Style.PRINT);

        gr.drawPolygon(new Polygon(true)
                .add(-2, SIZE)
                .add(-2, SIZE + 4)
                .add(2, SIZE + 4)
                .add(2, SIZE), Style.NORMAL);
        gr.drawPolygon(new Polygon(true)
                .add(-2, -SIZE)
                .add(-2, -SIZE - 4)
                .add(2, -SIZE - 4)
                .add(2, -SIZE), Style.NORMAL);

        gr.drawCircle(new Vector(-SIZE, -SIZE), new Vector(SIZE, SIZE), Style.NORMAL);
        gr.drawCircle(new Vector(-SIZE2, -SIZE2), new Vector(SIZE2, SIZE2), Style.PRINT);
        gr.drawCircle(new Vector(-PIN, -PIN), new Vector(PIN, PIN), Style.NORMAL);
        gr.drawText(new Vector(0, -SIZE * 2), name, Orientation.CENTERBOTTOM, Style.PRINT);
    }
}
