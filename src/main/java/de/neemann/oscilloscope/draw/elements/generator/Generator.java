package de.neemann.oscilloscope.draw.elements.generator;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicSignal;

import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;

/**
 * Abstraction of a function generator
 */
public class Generator extends Container<Generator> implements PeriodicSignal {

    private final SelectorKnob<Magnify> freq;
    private final SelectorKnob<Form> form;
    private final Poti freqFine;
    private final Poti phase;
    private final Poti amplitude;
    private final Switch<OffOn> power;
    private double freqency;

    private static ArrayList<Magnify> createFrequencies() {
        ArrayList<Magnify> f = new ArrayList<>();
        f.add(new Magnify(1));
        f.add(new Magnify(10));
        f.add(new Magnify(100));
        f.add(new Magnify(1000));
        f.add(new Magnify(10000));
        f.add(new Magnify(100000));
        f.add(new Magnify(1000000));
        return f;
    }

    /**
     * Creates a new function generator
     */
    public Generator() {
        super(SIZE * 27, SIZE * 10);

        freq = new SelectorKnob<Magnify>("Freq", 30).addAll(createFrequencies());
        freqFine = new Poti("Fine", 30);
        phase = new Poti("Phase", 30);
        amplitude = new Poti("Ampl.", 30);
        power = new Switch<OffOn>("Power").add(OffOn.values());
        form = new SelectorKnob<Form>("Shape", 30).addAll(Form.values());


        Observer freqChanged = () -> freqency = freq.getSelected().getMag() * Math.exp(freqFine.get() * Math.log(10));
        freq.addObserver(freqChanged);
        freqFine.addObserver(freqChanged);
        freqChanged.hasChanged();

        add(freq.setPos(SIZE * 18, SIZE * 3));
        add(freqFine.setPos(SIZE * 18, SIZE * 8));
        add(form.setPos(SIZE * 9, SIZE * 3));
        add(phase.setPos(SIZE * 9, SIZE * 8));
        add(power.setPos(SIZE * 25, SIZE));
        add(amplitude.setPos(SIZE * 25, SIZE * 8));

        add(new Input("Out").setPos(SIZE * 2, SIZE * 8));
        add(new Input("Trig").setPos(SIZE * 2, SIZE * 3));

    }

    @Override
    public double period() {
        return 1 / freqency;
    }

    @Override
    public double v(double t) {
        if (power.is(OffOn.Off))
            return 0;

        double arg = t * freqency + phase.get();
        double ampl = amplitude.get() * 10;
        switch (form.getSelected()) {
            case SINE:
                return Math.sin(2 * Math.PI * arg) * ampl;
            case SQUARE:
                return arg - Math.floor(arg) < 0.5 ? ampl : -ampl;
            case TRIANGLE:
                double vt = arg - Math.floor(arg);
                return vt < 0.5 ? ampl * (4 * vt - 1) : ampl * (4 * (1 - vt) - 1);
            case SAWTOOTH:
                double vs = arg - Math.floor(arg);
                return (1 - vs * 2) * ampl;
        }
        return 0;
    }

}
