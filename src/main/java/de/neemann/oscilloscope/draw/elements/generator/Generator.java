package de.neemann.oscilloscope.draw.elements.generator;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.*;

import java.awt.*;
import java.util.ArrayList;

import static de.neemann.oscilloscope.draw.elements.Switch.SIZE;
import static de.neemann.oscilloscope.draw.elements.Switch.SIZE2;

/**
 * Abstraction of a function generator
 */
public class Generator extends Container<Generator> {
    private static final double MAX_AMPL = 10;

    private final SelectorKnob<Magnify> freq;
    private final Switch<Form> form;
    private final Potentiometer freqFine;
    private final Potentiometer phase;
    private final Potentiometer offset;
    private final Potentiometer amplitude;
    private final PowerSwitch power;
    private final BNCOutput output;
    private final BNCOutput trigOut;
    private final String name;
    private final SignalProvider triggerOut;
    private final SignalProvider signalOut;

    private static ArrayList<Magnify> createFrequencies() {
        ArrayList<Magnify> f = new ArrayList<>();
        f.add(new Magnify(1));
        f.add(new Magnify(10));
        f.add(new Magnify(100));
        f.add(new Magnify(1000));
        f.add(new Magnify(10000));
        f.add(new Magnify(100000));
        return f;
    }

    /**
     * Creates a new function generator
     *
     * @param name the generators name
     */
    public Generator(String name) {
        super(SIZE * 27, SIZE * 10);
        this.name = name;

        triggerOut = new SignalProvider();
        signalOut = new SignalProvider();

        TriggerObserver triggerObserver = new TriggerObserver();
        SignalObserver signalObserver = new SignalObserver();

        freq = new SelectorKnob<Magnify>("Freq/Hz", 30).addAll(createFrequencies());
        freqFine = new Potentiometer("Freq Fine", 30);
        freq.addObserver(signalObserver);
        freq.addObserver(triggerObserver);
        freqFine.addObserver(signalObserver);
        freqFine.addObserver(triggerObserver);
        phase = new Potentiometer("Phase", 30);
        phase.addObserver(signalObserver);
        phase.addObserver(triggerObserver);
        offset = new Potentiometer("Offset", 30).set(0.5).setCenterZero();
        offset.addObserver(signalObserver);
        amplitude = new Potentiometer("Ampl.", 30);
        amplitude.addObserver(signalObserver);
        power = new PowerSwitch();
        power.addObserver(signalObserver);
        power.addObserver(triggerObserver);
        form = new Switch<Form>("Shape").add(Form.values());
        form.addObserver(signalObserver);

        setBackground(Color.WHITE);
        add(freq.setPos(SIZE * 19, SIZE * 3));
        add(freqFine.setPos(SIZE * 19, SIZE * 8));
        add(form.setPos(SIZE * 11, SIZE2));
        add(phase.setPos(SIZE * 7, SIZE * 8));
        add(offset.setPos(SIZE * 13, SIZE * 8));
        add(power.setPos(SIZE * 24 + SIZE2, SIZE));
        add(amplitude.setPos(SIZE * 25, SIZE * 8));

        output = new BNCOutput("Out", signalOut);
        add(output.setPos(SIZE * 2, SIZE * 8));
        trigOut = new BNCOutput("Trig", triggerOut);
        add(trigOut.setPos(SIZE * 2, SIZE * 3));
    }

    /**
     * @return the name of the generator
     */
    public String getName() {
        return name;
    }

    /**
     * @return the output connector
     */
    public BNCOutput getOutput() {
        return output;
    }

    /**
     * @return the trigger connector
     */
    public BNCOutput getTrigOutput() {
        return trigOut;
    }

    /**
     * @return the power switch
     */
    public PowerSwitch getPowerSwitch() {
        return power;
    }

    /**
     * @return the amplitude poti
     */
    public Potentiometer getAmplitude() {
        return amplitude;
    }

    /**
     * @return the frequency knob
     */
    public SelectorKnob<Magnify> setFrequencySwitch() {
        return freq;
    }

    /**
     * @return the frequency fine poti
     */
    public Potentiometer setFrequencyFinePoti() {
        return freqFine;
    }

    private double getOmega() {
        return 2 * Math.PI * freq.getSelected().getMag() * 1.001 * Math.exp(freqFine.get() * Math.log(10));
    }

    private class SignalObserver implements Observer {

        @Override
        public void hasChanged() {
            PeriodicSignal out = PeriodicSignal.GND;
            if (power.is(OffOn.On)) {
                double phase = Generator.this.phase.get() * 2 * Math.PI;
                double ampl = amplitude.get() * MAX_AMPL;
                double offs = (offset.get() - 0.5) * 2 * MAX_AMPL;
                switch (form.getSelected()) {
                    case SINE:
                        out = new Sine(ampl, getOmega(), phase, offs);
                        break;
                    case SQUARE:
                        out = new Square(ampl, getOmega(), phase, offs);
                        break;
                    case TRIANGLE:
                        out = new Triangle(ampl, getOmega(), phase, offs);
                        break;
                    case SAWTOOTH:
                        out = new Sawtooth(ampl, getOmega(), phase, offs);
                        break;
                }
            }
            signalOut.setSignal(out);
        }
    }

    private class TriggerObserver implements Observer {

        @Override
        public void hasChanged() {
            if (power.is(OffOn.On))
                triggerOut.setSignal(new Square(2.5, getOmega(), 0, 2.5));
            else
                triggerOut.setSignal(PeriodicSignal.GND);
        }
    }


}
