package de.neemann.oscilloscope.signal.primitives;

/**
 * The square signal
 */
public class Square extends Signal {
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
        super(ampl, w, phase, offset);
        this.frequency = w / 2 / Math.PI;
    }

    @Override
    public double v(double t) {
        double arg = t * frequency + getPhase();
        return (arg - Math.floor(arg) < 0.5 ? getAmplitude() : -getAmplitude()) + getOffset();
    }

}
