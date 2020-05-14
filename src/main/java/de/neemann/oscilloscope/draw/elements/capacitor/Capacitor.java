package de.neemann.oscilloscope.draw.elements.capacitor;

import de.neemann.oscilloscope.draw.elements.BNCInput;
import de.neemann.oscilloscope.draw.elements.BNCOutput;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.Switch;
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
        super(SIZE * 13, SIZE * 10);

        input = new BNCInput("");
        CapacitorModel m = new CapacitorModel(input.getSignalProvider());

        uc = new BNCOutput("", m.getVoltageCapacitor());
        ur = new BNCOutput("", m.getVoltageResistor());

        add(input.setPos(SIZE, SIZE * 5));
        add(uc.setPos(SIZE * 12, SIZE));
        add(ur.setPos(SIZE * 12, SIZE * 9));

        Switch<Integer> r = new Switch<Integer>("R/Ω").add(100).add(1000);
        r.addObserver(() -> m.setResistor(r.getSelected())).hasChanged();
        add(r.setPos(SIZE * 5, SIZE * 6 + SIZE2));

        Switch<Integer> c = new Switch<Integer>("C/nF").add(100).add(1000);
        c.addObserver(() -> m.setCapacitor(c.getSelected())).hasChanged();
        add(c.setPos(SIZE * 5, SIZE * 2 + SIZE2));

        //add(m.setDebugSwitch(new OnOffSwitch("Num")).setPos(0, 0));

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
                .add(SIZE * 8, SIZE)
                .add(SIZE * 8, SIZE * 2 + CAP_PAD), Style.PRINT);
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 8, SIZE * 8)
                .add(SIZE * 8, SIZE * 9)
                .add(SIZE, SIZE * 9)
                .add(SIZE, SIZE * 6), Style.PRINT);

        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 8 - SIZE2, SIZE * 5)
                .add(SIZE * 8 + SIZE2, SIZE * 5)
                .add(SIZE * 8 + SIZE2, SIZE * 8)
                .add(SIZE * 8 - SIZE2, SIZE * 8), Style.PRINT);

        gr.drawLine(new Vector(SIZE * 8 - SIZE, SIZE * 3 - CAP_PAD), new Vector(SIZE * 8 + SIZE, SIZE * 3 - CAP_PAD), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 8 - SIZE, SIZE * 2 + CAP_PAD), new Vector(SIZE * 8 + SIZE, SIZE * 2 + CAP_PAD), Style.PRINT);

        gr.drawLine(new Vector(SIZE * 8, SIZE * 3 - CAP_PAD), new Vector(SIZE * 8, SIZE * 5), Style.PRINT);


        gr.drawLine(new Vector(SIZE * 8, SIZE * 4), new Vector(SIZE * 12, SIZE * 4), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 12, SIZE * 2), new Vector(SIZE * 12, SIZE * 8), Style.PRINT);


        gr.drawLine(new Vector(SIZE * 8, SIZE), new Vector(SIZE * 12, SIZE), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 8, SIZE * 9), new Vector(SIZE * 12, SIZE * 9), Style.PRINT);

        dot(gr, SIZE * 8, SIZE);
        dot(gr, SIZE * 8, SIZE * 4);
        dot(gr, SIZE * 12, SIZE * 4);
        dot(gr, SIZE * 8, SIZE * 9);
    }

}
