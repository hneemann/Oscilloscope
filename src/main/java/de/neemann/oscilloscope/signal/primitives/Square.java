package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * The square signal
 */
public class Square implements PeriodicSignal {
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
    public Square(double ampl, double w, double phase, double offset) {
        this.ampl = ampl;
        this.phase = phase;
        this.frequency = w / 2 / Math.PI;
        this.period = 1 / frequency;
        this.offset = offset;
    }

    @Override
    public double v(double t) {
        double arg = t * frequency + phase;
        return (arg - Math.floor(arg) < 0.5 ? ampl : -ampl) + offset;
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
