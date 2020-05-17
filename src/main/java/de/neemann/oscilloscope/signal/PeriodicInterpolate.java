package de.neemann.oscilloscope.signal;

/**
 * Implements a table based periodic signal.
 */
public class PeriodicInterpolate implements PeriodicSignal {

    private final double period;
    private final double[] values;
    private final double mean;

    /**
     * Creates a new instance
     *
     * @param period the period
     * @param values the table values
     */
    public PeriodicInterpolate(double period, double[] values) {
        double sum = 0;
        for (double value : values) sum += value;
        mean = sum / values.length;
        this.period = period;
        this.values = values;
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double v(double t) {
        double tRel = t / period;
        tRel = tRel - Math.floor(tRel);

        int index0 = (int) (tRel * values.length);
        if (index0 >= values.length)
            index0 -= values.length;

        int index1 = index0 + 1;
        if (index1 >= values.length)
            index1 -= values.length;


        double v0 = values[index0];
        double v1 = values[index1];

        double tm = tRel * values.length - index0;

        return v0 + (v1 - v0) * tm;
    }

    @Override
    public double mean() {
        return mean;
    }
}
