package de.neemann.oscilloscope.signal;

public class Sin implements PeriodicSignal {

    private final double ampl;
    private final double freq;

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
