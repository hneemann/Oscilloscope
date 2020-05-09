package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

/**
 * The abstraction of the channel
 */
public class Channel {

    private SelectorKnob<Magnify> amplitude;
    private Poti pos;
    private Poti var;
    private Switch<Coupling> coupling;
    private BNCInput input;
    private Switch<OffOn> inv;
    private Switch<OffOn> mag5;

    /**
     * Sets a amplifier knob
     *
     * @param mag the amplifier knob
     * @return the given value for chained calls
     */
    public SelectorKnob<Magnify> setAmplitude(SelectorKnob<Magnify> mag) {
        this.amplitude = mag;
        return mag;
    }

    /**
     * Sets the pos poti
     *
     * @param pos the pos poti
     * @return the given value for chained calls
     */
    public Poti setPos(Poti pos) {
        this.pos = pos;
        return pos;
    }

    /**
     * Sets the var poti
     *
     * @param var the var poti
     * @return the given value for chained calls
     */
    public Poti setVar(Poti var) {
        this.var = var;
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
        return input;
    }

    /**
     * Sets the inv switch
     *
     * @param inv the inv switch
     * @return the given value for chained calls
     */
    public Switch<OffOn> setInv(Switch<OffOn> inv) {
        this.inv = inv;
        return inv;
    }

    /**
     * @return the amplitude value
     */
    public double getAmplitude() {
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
    public Poti getPosPoti() {
        return pos;
    }

    /**
     * @return the var value
     */
    public double getVar() {
        return var.get();
    }

    /**
     * @return the coupling
     */
    public Coupling getCoupling() {
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
    public boolean isInv() {
        if (inv == null)
            return false;
        return inv.is(OffOn.On);
    }

    /**
     * Sets the mag5 switch
     *
     * @param mag5 the mag 5 switch
     * @return the given value for chained calls
     */
    public Switch<OffOn> setMag5(Switch<OffOn> mag5) {
        this.mag5 = mag5;
        return mag5;
    }

    /**
     * @return true if mag5 is on
     */
    public boolean isMag5() {
        return mag5.is(OffOn.On);
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
    public Switch<OffOn> getInvSwitch() {
        return inv;
    }
}
