package de.neemann.oscilloscope.draw.elements.osco;

import de.neemann.oscilloscope.draw.elements.*;

/**
 * The horizontal unit
 */
public class Horizontal {

    private Poti pos;
    private Poti var;
    private Switch<OffOn> horiMag;
    private SelectorKnob<TimeBase> timeBase;

    /**
     * Sets the pos poty
     *
     * @param horiPos the pos poti
     * @return the given value for chained calls
     */
    public Poti setPos(Poti horiPos) {
        this.pos = horiPos;
        return horiPos;
    }

    /**
     * Sets the var poti
     *
     * @param horiVar the var poti
     * @return the given value for chained calls
     */
    public Poti setVar(Poti horiVar) {
        this.var = horiVar;
        return horiVar;
    }

    /**
     * Sets the mag switch
     *
     * @param horiMag the mag switch
     * @return the given value for chained calls
     */
    public Switch<OffOn> setMag(Switch<OffOn> horiMag) {
        this.horiMag = horiMag;
        return horiMag;
    }

    /**
     * Sets the time base knop
     *
     * @param timeBase the knob
     * @return the given value for chained calls
     */
    public SelectorKnob<TimeBase> setTimeBase(SelectorKnob<TimeBase> timeBase) {
        this.timeBase = timeBase;
        return timeBase;
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
     * @return tue if mag10 is on
     */
    public boolean isMag() {
        return horiMag.is(OffOn.On);
    }

    /**
     * @return time in sec per div
     */
    public double getTimePerDiv() {
        double tpd = timeBase.getSelected().getMag() / (1 + var.get() * 2);
        if (isMag())
            tpd /= 10;
        return tpd;
    }

    /**
     * @return the time base knob
     */
    public SelectorKnob<TimeBase> getTimeBaseKnob() {
        return timeBase;
    }

    /**
     * @return true if in X-Y mode
     */
    public boolean isXY() {
        return timeBase.getSelected().getMag() == 0;
    }

    /**
     * @return true if real time simulation is required
     */
    public boolean requiresRT() {
        return timeBase.getSelected().getMag() > 0.009;
    }
}
