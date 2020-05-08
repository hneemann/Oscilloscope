package de.neemann.oscilloscope.draw.elements.diode;

import de.neemann.oscilloscope.draw.elements.BNCInput;
import de.neemann.oscilloscope.draw.elements.BNCOutput;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;

import java.awt.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * The diode experimental setup
 */
public class Diode extends Container<Diode> {

    /**
     * Creates a new diode setup
     */
    public Diode() {
        super(SIZE * 10, SIZE * 10);

        DiodeModel m = new DiodeModel();

        add(new BNCInput("").setPos(SIZE * 1, SIZE * 5).setInputSetter(m::setInput));

        add(new BNCOutput("").setPos(SIZE * 9, SIZE * 1).setOutput(m::getVoltageDiode));
        add(new BNCOutput("").setPos(SIZE * 9, SIZE * 9).setOutput(m::getVoltageResistor));

        setBackground(Color.WHITE);
    }


    @Override
    public void drawToOrigin(Graphic gr) {
        super.drawToOrigin(gr);

        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 1, SIZE * 5)
                .add(SIZE * 1, SIZE * 1)
                .add(SIZE * 5, SIZE * 1)
                .add(SIZE * 5, SIZE * 2), Style.NORMAL);
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 5, SIZE * 8)
                .add(SIZE * 5, SIZE * 9)
                .add(SIZE * 1, SIZE * 9)
                .add(SIZE * 1, SIZE * 6), Style.NORMAL);

        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 5 - SIZE2, SIZE * 5)
                .add(SIZE * 5 + SIZE2, SIZE * 5)
                .add(SIZE * 5 + SIZE2, SIZE * 8)
                .add(SIZE * 5 - SIZE2, SIZE * 8), Style.NORMAL);

        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 5 - SIZE2, SIZE * 2)
                .add(SIZE * 5 + SIZE2, SIZE * 2)
                .add(SIZE * 5, SIZE * 3), Style.NORMAL);

        gr.drawLine(new Vector(SIZE * 5 - SIZE2, SIZE * 3), new Vector(SIZE * 5 + SIZE2, SIZE * 3), Style.NORMAL);
        gr.drawLine(new Vector(SIZE * 5, SIZE * 3), new Vector(SIZE * 5, SIZE * 5), Style.NORMAL);


        gr.drawLine(new Vector(SIZE * 5, SIZE * 4), new Vector(SIZE * 9, SIZE * 4), Style.NORMAL);
        gr.drawLine(new Vector(SIZE * 9, SIZE * 2), new Vector(SIZE * 9, SIZE * 8), Style.NORMAL);


        gr.drawLine(new Vector(SIZE * 5, SIZE * 1), new Vector(SIZE * 9, SIZE * 1), Style.NORMAL);
        gr.drawLine(new Vector(SIZE * 5, SIZE * 9), new Vector(SIZE * 9, SIZE * 9), Style.NORMAL);


    }
}
