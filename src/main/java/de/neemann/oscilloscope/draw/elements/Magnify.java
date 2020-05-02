package de.neemann.oscilloscope.draw.elements;

import java.util.Locale;

public class Magnify {

    private static final String[] UNIT = new String[]{"", "m", "u", "n"};
    private final double value;
    private final String str;

    public Magnify(double value) {
        this.value = value;
        str = toStr(value);
    }

    String toStr(double timeBase) {
        double t = timeBase;
        int u = 0;
        while (t < 0.1) {
            t *= 1000;
            u++;
        }
        String s = String.format(Locale.US, "%.1f", t);
        if (s.startsWith("0."))
            s = s.substring(1);
        if (s.endsWith(".0"))
            s = s.substring(0, s.length() - 2);
        s += UNIT[u];
        return s;
    }

    @Override
    public String toString() {
        return str;
    }

    public double getMag() {
        return value;
    }
}
