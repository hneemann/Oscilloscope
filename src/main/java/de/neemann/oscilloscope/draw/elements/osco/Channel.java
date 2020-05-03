package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

public class Channel {

    private SelectorKnob<Magnify> amplitude;
    private Poti pos;
    private Poti var;
    private Switch<Coupling> coupling;
    private Input input;
    private Switch<OffOn> inv;
    private Switch<OffOn> mag5;

    public SelectorKnob<Magnify> setAmplitude(SelectorKnob<Magnify> mag) {
        this.amplitude = mag;
        return mag;
    }

    public Poti setPos(Poti pos) {
        this.pos = pos;
        return pos;
    }

    public Poti setVar(Poti var) {
        this.var = var;
        return var;
    }

    public Switch<Coupling> setCoupling(Switch<Coupling> coupling) {
        this.coupling = coupling;
        return coupling;
    }

    public Input setInput(Input input) {
        this.input = input;
        return input;
    }

    public Switch<OffOn> setInv(Switch<OffOn> inv) {
        this.inv = inv;
        return inv;
    }

    public double getAmplitude() {
        if (isMag5())
            return amplitude.getSelected().getMag() / 5;
        else
            return amplitude.getSelected().getMag();
    }

    public double getPos() {
        return pos.get();
    }

    public Poti getPosPoti() {
        return pos;
    }

    public double getVar() {
        return var.get();
    }

    public Coupling getCoupling() {
        return coupling.getSelected();
    }

    public Input getInput() {
        return input;
    }

    public boolean isInv() {
        if (inv == null)
            return false;
        return inv.is(OffOn.On);
    }

    public Switch<OffOn> setMag5(Switch<OffOn> mag5) {
        this.mag5 = mag5;
        return mag5;
    }

    public boolean isMag5() {
        return mag5.is(OffOn.On);
    }
}
