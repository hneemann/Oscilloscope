package de.neemann.oscilloscope.signal;

/**
 * A sin signal
 */
public class Sin implements PeriodicSignal {

    private final double ampl;
    private final double freq;
    private final double phase;

    /**
     * Creates a new sine signals
     *
     * @param ampl the amplitude
     * @param freq the frequency
     */
    public Sin(double ampl, double freq) {
        this(ampl, freq, 0);
    }

    /**
     * Creates a new sine signals
     *
     * @param ampl  the amplitude
     * @param freq  the frequency
     * @param phase the phase
     */
    public Sin(double ampl, double freq, double phase) {
        this.ampl = ampl;
        this.freq = freq;
        this.phase = phase;
    }

    @Override
    public double v(double t) {
        return ampl * Math.sin(2 * Math.PI * freq * t + phase);
    }

    @Override
    public double period() {
        return 1 / freq;
    }
}
