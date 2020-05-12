package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.interpolate.Func;

/**
 * A periodic signal which is created by applying a function to a given signal.
 */
public class SignalFunc implements PeriodicSignal {
    private final PeriodicSignal s;
    private final Func f;

    /**
     * Creates a new instance
     *
     * @param s the periodic signal
     * @param f the function to apply on the signal
     */
    public SignalFunc(PeriodicSignal s, Func f) {
        this.s = s;
        this.f = f;
    }

    @Override
    public double v(double t) {
        return f.f(s.v(t));
    }

    @Override
    public double period() {
        return s.period();
    }

    @Override
    public double mean() {
        return f.f(s.mean());
    }

}
