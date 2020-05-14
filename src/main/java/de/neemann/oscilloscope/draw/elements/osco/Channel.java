package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;
import de.neemann.oscilloscope.gui.Observer;
import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.SignalProvider;

/**
 * The abstraction of the channel
 */
public class Channel implements Observer {
    private final SignalProvider output = new SignalProvider();
    private SelectorKnob<Magnify> amplitude;
    private Potentiometer pos;
    private Potentiometer var;
    private Switch<Coupling> coupling;
    private BNCInput input;
    private OnOffSwitch inv;
    private OnOffSwitch mag5;

    /**
     * Sets a amplifier knob
     *
     * @param mag the amplifier knob
     * @return the given value for chained calls
     */
    public SelectorKnob<Magnify> setAmplitude(SelectorKnob<Magnify> mag) {
        this.amplitude = mag;
        mag.addObserver(this);
        return mag;
    }

    /**
     * Sets the pos poti
     *
     * @param pos the pos poti
     * @return the given value for chained calls
     */
    public Potentiometer setPos(Potentiometer pos) {
        this.pos = pos;
        pos.addObserver(this);
        return pos;
    }

    /**
     * Sets the var poti
     *
     * @param var the var poti
     * @return the given value for chained calls
     */
    public Potentiometer setVar(Potentiometer var) {
        this.var = var;
        var.addObserver(this);
        return var;
    }

    /**
     * Sets the coupling switch
     *
     * @param coupling the coupling switch
     * @return the given value for chained calls
     */
    public Switch<Coupling> setCoupling(Switch<Coupling> coupling) {
        this.coupling = coupling;
        coupling.addObserver(this);
        return coupling;
    }

    /**
     * Sets the input
     *
     * @param input the input
     * @return the given value for chained calls
     */
    public BNCInput setInput(BNCInput input) {
        this.input = input;
        input.getSignalProvider().addObserver(this);
        return input;
    }

    /**
     * Sets the inv switch
     *
     * @param inv the inv switch
     * @return the given value for chained calls
     */
    public OnOffSwitch setInv(OnOffSwitch inv) {
        this.inv = inv;
        inv.addObserver(this);
        return inv;
    }

    /**
     * @return the amplitude value
     */
    private double getAmplitude() {
        if (isMag5())
            return amplitude.getSelected().getMag() / 5;
        else
            return amplitude.getSelected().getMag();
    }

    /**
     * @return the pos value
     */
    public double getPos() {
        return pos.get();
    }

    /**
     * @return the pos poti
     */
    public Potentiometer getPosPoti() {
        return pos;
    }

    /**
     * @return the var value
     */
    private double getVar() {
        return var.get();
    }

    /**
     * @return the coupling
     */
    private Coupling getCoupling() {
        return coupling.getSelected();
    }

    /**
     * @return the input
     */
    public BNCInput getInput() {
        return input;
    }

    /**
     * @return true if inv os on
     */
    private boolean isInv() {
        if (inv == null)
            return false;
        return inv.isOn();
    }

    /**
     * Sets the mag5 switch
     *
     * @param mag5 the mag 5 switch
     * @return the given value for chained calls
     */
    public OnOffSwitch setMag5(OnOffSwitch mag5) {
        this.mag5 = mag5;
        mag5.addObserver(this);
        return mag5;
    }

    /**
     * @return true if mag5 is on
     */
    private boolean isMag5() {
        return mag5.isOn();
    }

    /**
     * @return the coupling switch
     */
    public Switch<Coupling> getCouplingSwitch() {
        return coupling;
    }

    /**
     * @return the amplitude knob
     */
    public SelectorKnob<Magnify> getAmplitudeSwitch() {
        return amplitude;
    }

    /**
     * @return the inv switch
     */
    public OnOffSwitch getInvSwitch() {
        return inv;
    }

    @Override
    public void hasChanged() {
        output.setSignal(new Frontend(this));
    }

    /**
     * @return the output signal
     */
    public PeriodicSignal getSignal() {
        return output.getSignal();
    }

    private static final class Frontend implements PeriodicSignal {
        private final PeriodicSignal s;
        private final Coupling coupling;
        private final boolean isInv;
        private final double amplitude;
        private final double var;

        /**
         * Creates a new frontend
         *
         * @param channel the channel parameters
         */
        private Frontend(Channel channel) {
            this.s = channel.getInput().getSignalProvider().getSignal();
            coupling = channel.getCoupling();
            amplitude = channel.getAmplitude();
            var = channel.getVar();
            isInv = channel.isInv();
        }


        @Override
        public double v(double t) {
            if (coupling == Coupling.GND)
                return 0;
            else {
                double v = s.v(t);

                if (coupling == Coupling.AC)
                    v -= s.mean();

                if (isInv)
                    v = -v;
                return v / amplitude * (1 + var * 2);
            }
        }

        @Override
        public double period() {
            return s.period();
        }

        @Override
        public double mean() {
            if (coupling == Coupling.DC)
                return s.mean();
            return 0;
        }
    }

}
