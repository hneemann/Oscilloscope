package de.neemann.oscilloscope.signal;

/**
 * Creates a simple mean value.
 */
public class PeriodicSignalMean implements PeriodicSignal {
    private static final double FIL = 0.0001;
    private final PeriodicSignal parent;
    private double mean;

    /**
     * Creates a new instance
     *
     * @param parent the parent signal
     */
    public PeriodicSignalMean(PeriodicSignal parent) {
        this.parent = parent;
    }

    @Override
    public double period() {
        return parent.period();
    }

    @Override
    public double mean() {
        return mean;
    }

    @Override
    public double v(double t) {
        double v = parent.v(t);
        mean = v * FIL + (1 - FIL) * mean;
        return v;
    }
}
