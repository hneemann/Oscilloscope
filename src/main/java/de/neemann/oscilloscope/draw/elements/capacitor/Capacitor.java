package de.neemann.oscilloscope.draw.elements.capacitor;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;

import java.awt.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;
import static de.neemann.oscilloscope.draw.elements.diode.Diode.dot;

/**
 * The capacitor experimental setup
 */
public class Capacitor extends Container<Capacitor> {

    /**
     * capacitor padding
     */
    public static final int CAP_PAD = 5;

    private final BNCInput input;
    private final BNCOutput uc;
    private final BNCOutput ur;

    /**
     * Creates a new capacitor setup
     */
    public Capacitor() {
        super(SIZE * 10, SIZE * 10);

        input = new BNCInput("");
        CapacitorModel m = new CapacitorModel(input.getSignalProvider());

        uc = new BNCOutput("", m.getVoltageCapacitor());
        ur = new BNCOutput("", m.getVoltageResistor());

        add(input.setPos(SIZE, SIZE * 5));
        add(uc.setPos(SIZE * 9, SIZE));
        add(ur.setPos(SIZE * 9, SIZE * 9));

//        Switch<OffOn> debugSwitch = new Switch<OffOn>("").add(OffOn.values());
//        add(debugSwitch.setPos(0, 0));
//        m.setDebugSwitch(debugSwitch);

        setBackground(Color.WHITE);
    }

    /**
     * @return the input connector
     */
    public BNCInput getInput() {
        return input;
    }

    /**
     * @return the resistance voltage connector
     */
    public BNCOutput getVoltRes() {
        return ur;
    }

    /**
     * @return the diode voltage connector
     */
    public BNCOutput getVoltCapacitor() {
        return uc;
    }

    @Override
    public void drawToOrigin(Graphic gr) {
        super.drawToOrigin(gr);

        gr.drawPolygon(new Polygon(false)
                .add(SIZE, SIZE * 5)
                .add(SIZE, SIZE)
                .add(SIZE * 5, SIZE)
                .add(SIZE * 5, SIZE * 2 + CAP_PAD), Style.PRINT);
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 5, SIZE * 8)
                .add(SIZE * 5, SIZE * 9)
                .add(SIZE, SIZE * 9)
                .add(SIZE, SIZE * 6), Style.PRINT);

        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 5 - SIZE2, SIZE * 5)
                .add(SIZE * 5 + SIZE2, SIZE * 5)
                .add(SIZE * 5 + SIZE2, SIZE * 8)
                .add(SIZE * 5 - SIZE2, SIZE * 8), Style.PRINT);

        gr.drawLine(new Vector(SIZE * 5 - SIZE, SIZE * 3 - CAP_PAD), new Vector(SIZE * 5 + SIZE, SIZE * 3 - CAP_PAD), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 5 - SIZE, SIZE * 2 + CAP_PAD), new Vector(SIZE * 5 + SIZE, SIZE * 2 + CAP_PAD), Style.PRINT);

        gr.drawLine(new Vector(SIZE * 5, SIZE * 3 - CAP_PAD), new Vector(SIZE * 5, SIZE * 5), Style.PRINT);


        gr.drawLine(new Vector(SIZE * 5, SIZE * 4), new Vector(SIZE * 9, SIZE * 4), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 9, SIZE * 2), new Vector(SIZE * 9, SIZE * 8), Style.PRINT);


        gr.drawLine(new Vector(SIZE * 5, SIZE), new Vector(SIZE * 9, SIZE), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 5, SIZE * 9), new Vector(SIZE * 9, SIZE * 9), Style.PRINT);

        dot(gr, SIZE * 5, SIZE);
        dot(gr, SIZE * 5, SIZE * 4);
        dot(gr, SIZE * 9, SIZE * 4);
        dot(gr, SIZE * 5, SIZE * 9);
    }

}
