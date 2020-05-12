package de.neemann.oscilloscope.signal.primitives;

import de.neemann.oscilloscope.signal.PeriodicSignal;

/**
 * A sine signal
 */
public final class Sine implements PeriodicSignal {
    private final double ampl;
    private final double w;
    private final double phase;
    private final double period;
    private final double offset;

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Sine(double ampl, double w, double phase, double offset) {
        this.ampl = ampl;
        this.w = w;
        this.phase = phase;
        this.period = 2 * Math.PI / w;
        this.offset = offset;
    }

    @Override
    public double period() {
        return period;
    }

    @Override
    public double v(double t) {
        return ampl * Math.sin(w * t + phase) + offset;
    }

    @Override
    public double mean() {
        return offset;
    }

    /**
     * @return the amplitude
     */
    public double getAmpl() {
        return ampl;
    }

    /**
     * @return the circular frequency omega
     */
    public double getOmega() {
        return w;
    }

    /**
     * @return the phase
     */
    public double getPhase() {
        return phase;
    }

    /**
     * @return The sine offset
     */
    public double getOffset() {
        return offset;
    }
}
