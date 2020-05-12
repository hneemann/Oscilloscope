package de.neemann.oscilloscope.signal.interpolate;

/**
 * Linear interpolation of a given function.
 * If a function is very complex to evaluate, it my be necessary to use this
 * class. It allows to calculate the function values only once, and from there on
 * using a linear interpolation of the function.
 */
public class InterpolateLinear implements Func {
    private final double xmin;
    private final double xmax;
    private final double dx;
    private final int points;
    private final double[] values;

    /**
     * Creates a new instance
     *
     * @param xmin   min x value of the interpolation interval
     * @param xmax   max x value of the interpolation interval
     * @param points number of interpolation points used
     * @param func   the function to interpolate
     */
    public InterpolateLinear(double xmin, double xmax, int points, Func func) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.dx = xmax - xmin;
        this.points = points;
        values = new double[points];
        for (int i = 0; i < points; i++) {
            double x = xmin + dx * i / (points - 1);
            values[i] = func.f(x);
        }
    }

    @Override
    public double f(double x) {
        if (x < xmin)
            return values[0];
        else if (x >= xmax)
            return values[points - 1];

        double index = (x - xmin) * (points - 1) / dx;
        int i = (int) index;
        if (i >= points - 1)
            i = points - 2;

        double v0 = values[i];
        double v1 = values[i + 1];

        return v0 + (v1 - v0) * (index - i);
    }

}
