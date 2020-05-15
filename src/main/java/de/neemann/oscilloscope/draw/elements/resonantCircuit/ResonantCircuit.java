package de.neemann.oscilloscope.draw.elements.resonantCircuit;

import de.neemann.oscilloscope.gui.Debug;
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
    private final Switch<Integer> capSwitch;
    private final Switch<Integer> indSwitch;

    /**
     * Creates a new capacitor setup
     */
    public ResonantCircuit() {
        super(SIZE * 20, SIZE * 10);

        input = new BNCInput("");
        ResonantCircuitModel m = new ResonantCircuitModel(input.getSignalProvider());

        ur = new BNCOutput("", m.getVoltageResistor());
        resSwitch = new Switch<Integer>("R/Ω").add(50).add(100).add(500);
        capSwitch = new Switch<Integer>("C/nF").add(100).add(500).add(1000);
        indSwitch = new Switch<Integer>("L/mH").add(10).add(50).add(100);

        add(input.setPos(SIZE, SIZE * 5));
        add(ur.setPos(SIZE * 19, SIZE * 5));
        add(resSwitch.setPos(SIZE * 14 - SIZE2, SIZE * 4 - SIZE2));
        add(capSwitch.setPos(SIZE * 10 - SIZE2, SIZE * 4 - SIZE2));
        add(indSwitch.setPos(SIZE * 6 - SIZE2, SIZE * 4 - SIZE2));

        resSwitch.addObserver(() -> m.setResistor(resSwitch.getSelected())).hasChanged();
        capSwitch.addObserver(() -> m.setCapacitor(capSwitch.getSelected())).hasChanged();
        indSwitch.addObserver(() -> m.setInductor(indSwitch.getSelected())).hasChanged();

        if (Debug.isDebug())
            add(m.setDebugSwitch(new OnOffSwitch("Num")).setPos(0, 0));

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
                .add(SIZE * 4, SIZE), Style.PRINT);

        // L
        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 4, SIZE - SIZE2)
                .add(SIZE * 4, SIZE + SIZE2)
                .add(SIZE * 7, SIZE + SIZE2)
                .add(SIZE * 7, SIZE - SIZE2), Style.PRINT_FILLED);

        // wire L to C
        gr.drawLine(new Vector(SIZE * 7, SIZE), new Vector(SIZE * 10 + CAP_PAD, SIZE), Style.PRINT);

        // C
        gr.drawLine(new Vector(SIZE * 10 + CAP_PAD, 0), new Vector(SIZE * 10 + CAP_PAD, SIZE + SIZE), Style.PRINT);
        gr.drawLine(new Vector(SIZE * 11 - CAP_PAD, 0), new Vector(SIZE * 11 - CAP_PAD, SIZE + SIZE), Style.PRINT);

        // wire C to R
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 11 - CAP_PAD, SIZE)
                .add(SIZE * 16, SIZE)
                .add(SIZE * 16, SIZE * 3 + SIZE2), Style.PRINT);

        // Resistor
        gr.drawPolygon(new Polygon(true)
                .add(SIZE * 16 - SIZE2, SIZE * 3 + SIZE2)
                .add(SIZE * 16 + SIZE2, SIZE * 3 + SIZE2)
                .add(SIZE * 16 + SIZE2, SIZE * 6 + SIZE2)
                .add(SIZE * 16 - SIZE2, SIZE * 6 + SIZE2), Style.PRINT);

        // ground wire connector to R
        gr.drawPolygon(new Polygon(false)
                .add(SIZE, SIZE * 6)
                .add(SIZE, SIZE * 9)
                .add(SIZE * 16, SIZE * 9)
                .add(SIZE * 16, SIZE * 7 - SIZE2), Style.PRINT);

        // ground wire connector to out
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 16, SIZE * 9)
                .add(SIZE * 19, SIZE * 9)
                .add(SIZE * 19, SIZE * 6), Style.PRINT);

        // R wire connector to out
        gr.drawPolygon(new Polygon(false)
                .add(SIZE * 16, SIZE)
                .add(SIZE * 19, SIZE)
                .add(SIZE * 19, SIZE * 5), Style.PRINT);


        dot(gr, SIZE * 16, SIZE);
        dot(gr, SIZE * 16, SIZE * 9);
    }

}
