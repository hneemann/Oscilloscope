package de.neemann.oscilloscope.signal;

/**
 * A sine signal
 */
public final class Sine extends PeriodicSignal {
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

    @Override
    public SinParams getSinParams() {
        return new SinParams(ampl, offset, w, phase);
    }
}
