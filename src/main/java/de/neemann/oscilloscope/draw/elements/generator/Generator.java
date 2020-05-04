package de.neemann.oscilloscope.draw.elements.generator;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicSignal;

import java.awt.*;
import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * Abstraction of a function generator
 */
public class Generator extends Container<Generator> implements PeriodicSignal {
    private static final double MAX_AMPL = 10;

    private final SelectorKnob<Magnify> freq;
    private final Switch<Form> form;
    private final Poti freqFine;
    private final Poti phase;
    private final Poti offset;
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
        offset = new Poti("Offset", 30).set(0.5);
        amplitude = new Poti("Ampl.", 30);
        power = new PowerSwitch();
        form = new Switch<Form>("Shape").add(Form.values());


        Observer freqChanged = () -> freqency = freq.getSelected().getMag() * Math.exp(freqFine.get() * Math.log(10));
        freq.addObserver(freqChanged);
        freqFine.addObserver(freqChanged);
        freqChanged.hasChanged();

        setBackground(Color.WHITE);
        add(freq.setPos(SIZE * 19, SIZE * 3));
        add(freqFine.setPos(SIZE * 19, SIZE * 8));
        add(form.setPos(SIZE * 11, SIZE2));
        add(phase.setPos(SIZE * 7, SIZE * 8));
        add(offset.setPos(SIZE * 13, SIZE * 8));
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
        double ampl = amplitude.get() * MAX_AMPL;
        switch (form.getSelected()) {
            case SINE:
                return Math.sin(2 * Math.PI * arg) * ampl + mean();
            case SQUARE:
                return (arg - Math.floor(arg) < 0.5 ? ampl : -ampl) + mean();
            case TRIANGLE:
                double vt = arg - Math.floor(arg);
                return (vt < 0.5 ? ampl * (4 * vt - 1) : ampl * (4 * (1 - vt) - 1)) + mean();
            case SAWTOOTH:
                double vs = arg - Math.floor(arg);
                return (1 - vs * 2) * ampl + mean();
        }
        return 0;
    }

    @Override
    public double mean() {
        return (offset.get() - 0.5) * 2 * MAX_AMPL;
    }

}
