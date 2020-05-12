package de.neemann.oscilloscope.signal.interpolate;

/**
 * Cubic interpolation of a given function.
 * If a function is very complex to evaluate, it my be necessary to use this
 * class. It allows to calculate the function values only once, and from there on
 * using a linear interpolation of the function.
 */
public class InterpolateCubic implements Func {
    private final double xmin;
    private final double xmax;
    private final double dx;
    private final int points;
    private final double[] a;
    private final double[] b;
    private final double[] c;
    private final double[] d;

    /**
     * Creates a new instance
     *
     * @param xmin   min x value of the interpolation interval
     * @param xmax   max x value of the interpolation interval
     * @param points number of interpolation points used
     * @param func   the function to interpolate
     * @param deriv  the derivative of the function to interpolate
     */
    public InterpolateCubic(double xmin, double xmax, int points, Func func, Func deriv) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.dx = xmax - xmin;
        this.points = points;
        d = new double[points];
        double[] g = new double[points];
        for (int i = 0; i < points; i++) {
            double x = xmin + dx * i / (points - 1);
            d[i] = func.f(x);
            g[i] = deriv.f(x) * dx / (points - 1);
        }

        a = new double[points];
        b = new double[points];
        c = new double[points];

        for (int i0 = 0; i0 < points - 1; i0++) {
            int i1 = i0 + 1;
            a[i0] = g[i0] + g[i1] + 2 * (d[i0] - d[i1]);
            b[i0] = -2 * g[i0] - g[i1] - 3 * (d[i0] - d[i1]);
            c[i0] = g[i0];
        }
    }

    @Override
    public double f(double x) {
        if (x < xmin)
            return d[0];
        else if (x >= xmax)
            return d[points - 1];

        double index = (x - xmin) * (points - 1) / dx;
        int i = (int) index;
        if (i >= points)
            i = points - 1;

        double tm = index - i;
        return a[i] * tm * tm * tm + b[i] * tm * tm + c[i] * tm + d[i];
    }

}
