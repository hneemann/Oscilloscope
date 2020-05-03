package de.neemann.oscilloscope.draw.elements;

import java.util.Locale;

/**
 * Abstraction of a magnitude
 */
public class Magnify {

    private static final String[] SMALL = new String[]{"", "m", "u", "n"};
    private static final String[] LARGE = new String[]{"", "k", "M", "G"};
    private final double value;
    private final String str;

    /**
     * Create a new magnify item
     *
     * @param value the mag value
     */
    public Magnify(double value) {
        this.value = value;
        str = toStr(value);
    }

    String toStr(double timeBase) {
        double t = timeBase;
        String prefix = "";
        if (t < 0.1) {
            int u = 0;
            while (t < 0.1) {
                t *= 1000;
                u++;
            }
            prefix = SMALL[u];
        } else {
            int u = 0;
            while (t >= 1000) {
                t /= 1000;
                u++;
            }
            prefix = LARGE[u];
        }
        String s = String.format(Locale.US, "%.1f", t);
        if (s.startsWith("0."))
            s = s.substring(1);
        if (s.endsWith(".0"))
            s = s.substring(0, s.length() - 2);

        return s+prefix;
    }

    @Override
    public String toString() {
        return str;
    }

    /**
     * @return the mag value
     */
    public double getMag() {
        return value;
    }
}
