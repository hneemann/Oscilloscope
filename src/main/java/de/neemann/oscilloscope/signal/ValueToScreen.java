package de.neemann.oscilloscope.signal;

/**
 * Transfers a voltage to a pixel on the screen
 */
public class ValueToScreen implements PeriodicSignal {
    private final PeriodicSignal sig;
    private final double pos;
    private final int divs;
    private final int ofs;
    private final int pixels;
    private final int max;
    private final int min;

    /**
     * Creates a new screen transformation
     *
     * @param sig    the base signal
     * @param pos    the pos potentiometer
     * @param divs   the number of divs on the screen
     * @param pixels pixels available on screen
     */
    public ValueToScreen(PeriodicSignal sig, double pos, int divs, int pixels) {
        this.sig = sig;
        this.pos = pos;
        this.divs = divs;
        this.max = divs * 2;
        this.min = -divs;
        this.pixels = pixels;
        ofs = divs / 2;
    }

    @Override
    public double v(double t) {
        double div = sig.v(t) + ofs + (pos - 0.5) * 20;

        if (div > max)
            div = max;
        else if (div < min)
            div = min;

        return div * pixels / divs;
    }

    @Override
    public double period() {
        return sig.period();
    }

    @Override
    public double mean() {
        return sig.mean() + ofs + (pos - 0.5) * 20;
    }
}
