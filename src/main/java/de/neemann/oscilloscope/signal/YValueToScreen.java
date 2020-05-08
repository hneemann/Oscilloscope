package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.PotiInterface;

/**
 * Transfers a voltage ti a pixel number on the screen
 */
public class YValueToScreen implements ToScreen {
    private final PotiInterface pos;
    private final int divs;
    private final int ofs;

    /**
     * Creates a new screen transformation
     *
     * @param pos  the pos poti
     * @param divs the number of divs on the screen
     */
    public YValueToScreen(PotiInterface pos, int divs) {
        this.pos = pos;
        this.divs = divs;
        ofs = divs / 2;
    }

    @Override
    public int v(double v, int pixels) {
        double div = -v + ofs + (0.5-pos.get()) * 20;
        return (int) (div * pixels / divs);
    }
}
