package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.draw.elements.DoubleHelper;
import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * Used to add two signals
 */
public class Sum implements PeriodicSignal {

    private final PeriodicSignal a;
    private final PeriodicSignal b;
    private final double period;
    private final double mean;

    /**
     * Creates a new sum signal
     *
     * @param a signal a
     * @param b signal b
     */
    public Sum(PeriodicSignal a, PeriodicSignal b) {
        this.a = a;
        this.b = b;
        double p1 = a.period();
        double p2 = b.period();
        if (DoubleHelper.equal(p1, p2)) {
            this.period = (p1 + p2) / 2;
        } else {
            this.period = p1 * p2 / Math.abs(p1 - p2);
        }
        mean = a.mean() + b.mean();
    }


    @Override
    public double v(double t) {
        return a.v(t) + b.v(t);
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double mean() {
        return mean;
    }
}
