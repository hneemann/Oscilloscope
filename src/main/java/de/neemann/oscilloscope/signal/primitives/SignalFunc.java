package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;
import de.neemann.oscilloscope.signal.interpolate.Func;

/**
 * A periodic signal which is created by applying a function to a given signal.
 */
public class SignalFunc implements PeriodicSignal {
    private final PeriodicSignal s;
    private final Func f;
    private final double mean;

    /**
     * Creates a new instance
     *
     * @param s    the periodic signal
     * @param f    the function to apply on the signal
     * @param mean the mean value used for AC coupling
     */
    public SignalFunc(PeriodicSignal s, Func f, double mean) {
        this.s = s;
        this.f = f;
        this.mean = mean;
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
        return mean;
    }

}
