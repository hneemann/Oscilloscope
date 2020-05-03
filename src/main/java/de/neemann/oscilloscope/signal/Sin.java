package de.neemann.oscilloscope.signal;

/**
 * A sin signal
 */
public class Sin implements PeriodicSignal {

    private final double ampl;
    private final double freq;

    /**
     * Creates a new sine signals
     *
     * @param ampl the amplitude
     * @param freq the frequency
     */
    public Sin(double ampl, double freq) {
        this.ampl = ampl;
        this.freq = freq;
    }

    @Override
    public double v(double t) {
        return ampl * Math.sin(2 * Math.PI * freq * t);
    }

    @Override
    public double period() {
        return 1 / freq;
    }
}
