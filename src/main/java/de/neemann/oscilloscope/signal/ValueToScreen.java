package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.draw.elements.Poti;

final class ValueToScreen implements ToScreen {
    private final Poti pos;
    private final int divs;
    private final int ofs;

    public ValueToScreen(Poti pos, int divs) {
        this.pos = pos;
        this.divs = divs;
        ofs = divs / 2;
    }

    @Override
    public int v(double v, int pixels) {
        double div = v + ofs + (pos.get() - 0.5) * 20;
        return (int) (div * pixels / divs);
    }
}
