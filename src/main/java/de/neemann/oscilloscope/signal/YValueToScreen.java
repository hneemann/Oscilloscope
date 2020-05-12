package de.neemann.oscilloscope.signal;

/**
 * Transfers a voltage ti a pixel number on the screen
 */
public class YValueToScreen implements ToScreen {
    private final double pos;
    private final int divs;
    private final int ofs;
    private final int max;
    private final int min;

    /**
     * Creates a new screen transformation
     *
     * @param pos  the pos poti
     * @param divs the number of divs on the screen
     */
    public YValueToScreen(double pos, int divs) {
        this.pos = pos;
        this.divs = divs;
        this.max = divs * 2;
        this.min = -divs;
        ofs = divs / 2;
    }

    @Override
    public int v(double v, int pixels) {
        double div = -v + ofs + (0.5 - pos) * 20;

        if (div > max)
            div = max;
        else if (div < min)
            div = min;

        return (int) (div * pixels / divs);
    }
}
