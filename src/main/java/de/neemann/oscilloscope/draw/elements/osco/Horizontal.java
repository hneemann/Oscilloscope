package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

public class Horizontal {

    private Poti pos;
    private Poti var;
    private Switch<OffOn> horiMag;
    private SelectorKnob<TimeBase> timeBase;

    public Poti setPos(Poti horiPos) {
        this.pos = horiPos;
        return horiPos;
    }

    public Poti setVar(Poti horiVar) {
        this.var = horiVar;
        return horiVar;
    }

    public Switch<OffOn> setMag(Switch<OffOn> horiMag) {
        this.horiMag = horiMag;
        return horiMag;
    }

    public SelectorKnob<TimeBase> setTimeBase(SelectorKnob<TimeBase> timeBase) {
        this.timeBase = timeBase;
        return timeBase;
    }

    public double getPos() {
        return pos.get();
    }

    public double getVar() {
        return var.get();
    }

    public boolean isMag() {
        return horiMag.is(OffOn.On);
    }

    public double getTimeBase() {
        return timeBase.getSelected().getMag();
    }
}
