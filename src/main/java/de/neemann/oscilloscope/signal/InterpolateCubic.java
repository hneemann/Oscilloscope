package de.neemann.oscilloscope.signal;

/**
 * Implements a table based periodic signal.
 * Uses a very basic cubic interpolation make the curve more smooth.
 * ToDo: Add a proper B-spline interpolation!
 */
public class InterpolateCubic extends Interpolate {

    private double period;
    private double[] a;
    private double[] b;
    private double[] c;
    private double[] d;
    private double mean;

    @Override
    public void setValues(double period, double[] v) {
        double sum = 0;
        double[] g = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            sum += v[i];
            double dy = v[inc(i, v.length)] - v[dec(i, v.length)];
            g[i] = dy / 2;
        }

        double[] a = new double[v.length];
        double[] b = new double[v.length];
        double[] c = new double[v.length];
        double[] d = new double[v.length];

        for (int i0 = 0; i0 < v.length; i0++) {
            int i1 = inc(i0, v.length);
            a[i0] = g[i0] + g[i1] + 2 * (v[i0] - v[i1]);
            b[i0] = -2 * g[i0] - g[i1] - 3 * (v[i0] - v[i1]);
            c[i0] = g[i0];
            d[i0] = v[i0];
        }

        mean = sum / v.length;
        this.period = period;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;

        hasChanged();
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
