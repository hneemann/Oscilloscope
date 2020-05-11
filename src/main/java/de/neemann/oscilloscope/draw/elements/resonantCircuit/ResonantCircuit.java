package de.neemann.oscilloscope.draw.elements.resonantCircuit;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.graphics.Graphic;
import de.neemann.oscilloscope.draw.graphics.Polygon;
import de.neemann.oscilloscope.draw.graphics.Style;
import de.neemann.oscilloscope.draw.graphics.Vector;

import java.awt.*;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;
import static de.neemann.oscilloscope.draw.elements.capacitor.Capacitor.CAP_PAD;
import static de.neemann.oscilloscope.draw.elements.diode.Diode.dot;

/**
 * The capacitor experimental setup
 */
public class ResonantCircuit extends Container<ResonantCircuit> {

    private final BNCInput input;
    private final BNCOutput ur;
    private final Switch<Integer> resSwitch;

    /**
     * Creates a new capacitor setup
     */
    public ResonantCircuit() {
        super(SIZE * 15, SIZE * 10);

        ResonantCircuitModel m = new ResonantCircuitModel();

        input = new BNCInput("");
        ur = new BNCOutput("");
        resSwitch = new Switch<Integer>("R/Ω").add(100).add(500).add(1000);

        add(input.setPos(SIZE, SIZE * 5).setInputSetter(m::setInput));
        add(ur.setPos(SIZE * 14, SIZE * 5).setOutput(m::getVoltageResistor));
        add(resSwitch.setPos(SIZE * 9 - SIZE2, SIZE * 4 - SIZE2));

        m.setResistor(resSwitch.getSelected());
        resSwitch.addObserver(() -> m.setResistor(resSwitch.getSelected()));

//        Switch<OffOn> debugSwitch = new Switch<OffOn>("").add(OffOn.values());
//        m.setDebugSwitch(debugSwitch);
//        add(debugSwitch);

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

    @Override
    public void drawToOrigin(Graphic gr) {
        super.drawToOrigin(gr);

        // wire to L
        gr.drawPolygon(new Polygon(false)
                .add(SIZE, SIZE * 5)
                .add(SIZE, SIZE)
                .add(SIZE * 3, SIZE), Style.PRINT);

        // L
        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 3, SIZE - SIZE2)
                .add(SIZE * 3, SIZE + SIZE2)
                .add(SIZE * 6, SIZE + SIZE2)
                .add(SIZE * 6, SIZE - SIZE2), Style.PRINT_FILLED);

        // wire L to C
        gr.drawLine(new Vector(SIZE * 6, SIZE), new Vector(SIZE * 8 + CAP_PAD, SIZE), Style.PRINT);

        // C
        gr.drawLine(new Vector(SIZE * 8 + CAP_PAD, 0), new Vector(SIZE * 8 + CAP_PAD, SIZE + SIZE), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 9 - CAP_PAD, 0), new Vector(SIZE * 9 - CAP_PAD, SIZE + SIZE), Style.PRINT);

        // wire C to R
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 9 - CAP_PAD, SIZE)
                .add(SIZE * 11, SIZE)
                .add(SIZE * 11, SIZE * 3 + SIZE2), Style.PRINT);

        // Resistor
        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 11 - SIZE2, SIZE * 3 + SIZE2)
                .add(SIZE * 11 + SIZE2, SIZE * 3 + SIZE2)
                .add(SIZE * 11 + SIZE2, SIZE * 6 + SIZE2)
                .add(SIZE * 11 - SIZE2, SIZE * 6 + SIZE2), Style.PRINT);

        // ground wire connector to R
        gr.drawPolygon(new Polygon(false)
                .add(SIZE, SIZE * 6)
                .add(SIZE, SIZE * 9)
                .add(SIZE * 11, SIZE * 9)
                .add(SIZE * 11, SIZE * 7 - SIZE2), Style.PRINT);

        // ground wire connector to out
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 11, SIZE * 9)
                .add(SIZE * 14, SIZE * 9)
                .add(SIZE * 14, SIZE * 6), Style.PRINT);

        // R wire connector to out
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 11, SIZE)
                .add(SIZE * 14, SIZE)
                .add(SIZE * 14, SIZE * 5), Style.PRINT);


        dot(gr, SIZE * 11, SIZE);
        dot(gr, SIZE * 11, SIZE * 9);
    }

}
