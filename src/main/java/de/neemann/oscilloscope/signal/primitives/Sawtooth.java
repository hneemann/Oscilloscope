package de.neemann.oscilloscope.signal.primitives;

/**
 * The sawtooth signal
 */
public class Sawtooth extends Signal {
    private final double frequency;

    /**
     * Creates a new instance
     *
     * @param ampl   the amplitude
     * @param w      the circular frequency
     * @param phase  the phase
     * @param offset the offset
     */
    public Sawtooth(double ampl, double w, double phase, double offset) {
        super(ampl, w, phase, offset);
        this.frequency = w / 2 / Math.PI;
    }

    @Override
    public double v(double t) {
        double arg = t * frequency + getPhase();
        double vs = arg - Math.floor(arg);
        return (vs * 2 - 1) * getAmplitude() + getOffset();
    }
}
