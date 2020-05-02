package de.neemann.oscilloscope.signal;

public class Sin implements Signal {

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
}
