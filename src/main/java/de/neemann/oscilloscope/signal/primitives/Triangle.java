package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * The triangle signal
 */
public class Triangle implements PeriodicSignal {
    private final double ampl;
    private final double phase;
    private final double period;
    private final double offset;
    private final double frequency;

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Triangle(double ampl, double w, double phase, double offset) {
        this.ampl = ampl;
        this.phase = phase;
        this.frequency = w / 2 / Math.PI;
        this.period = 1 / frequency;
        this.offset = offset;
    }

    @Override
    public double v(double t) {
        double arg = t * frequency + phase;
        double vt = arg - Math.floor(arg);
        return (vt < 0.5 ? ampl * (4 * vt - 1) : ampl * (4 * (1 - vt) - 1)) + offset;
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double mean() {
        return offset;
    }
}
