package de.neemann.oscilloscope.signal;

/**
 * Implements a table based periodic signal.
 * Uses a very basic cubic interpolation make the curve more smooth.
 * ToDo: Add a proper B-spline interpolation!
 */
public class InterpolateCubic implements PeriodicSignal {
    private final double period;
    private final double[] a;
    private final double[] b;
    private final double[] c;
    private final double[] d;
    private final double mean;

    /**
     * Creates a new instance
     *
     * @param period the period
     * @param v      the table values
     */
    public InterpolateCubic(double period, double[] v) {
        this.period = period;

        double sum = 0;
        double[] g = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            sum += v[i];
            double dy = v[inc(i, v.length)] - v[dec(i, v.length)];
            g[i] = dy / 2;
        }
        mean = sum / v.length;

        a = new double[v.length];
        b = new double[v.length];
        c = new double[v.length];
        d = new double[v.length];

        for (int i0 = 0; i0 < v.length; i0++) {
            int i1 = inc(i0, v.length);
            a[i0] = g[i0] + g[i1] + 2 * (v[i0] - v[i1]);
            b[i0] = -2 * g[i0] - g[i1] - 3 * (v[i0] - v[i1]);
            c[i0] = g[i0];
            d[i0] = v[i0];
        }
    }

    private int dec(int i, int length) {
        if (i == 0)
            return length - 1;
        return --i;
    }

    private int inc(int i, int length) {
        i++;
        if (i == length)
            i = 0;
        return i;
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double v(double t) {
        double tRel = t / period;
        tRel = tRel - Math.floor(tRel);

        int i = (int) (tRel * a.length);
        if (i >= a.length)
            i -= a.length;

        double tm = tRel * a.length - i;
        return a[i] * tm * tm * tm + b[i] * tm * tm + c[i] * tm + d[i];
    }

    @Override
    public double mean() {
        return mean;
    }
}
