package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

public class Channel {

    private SelectorKnob<Magnify> mag;
    private Poti pos;
    private Poti var;
    private Switch<Coupling> coupling;
    private Input input;
    private Switch<OffOn> inv;

    public SelectorKnob<Magnify> setMag(SelectorKnob<Magnify> mag) {
        this.mag = mag;
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

    public double getMag() {
        return mag.getSelected().getMag();
    }

    public double getPos() {
        return pos.get();
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
}
