package de.neemann.oscilloscope.draw.elements;

import java.util.Locale;

/**
 * Abstraction of a magnitude
 */
public class Magnify {

    private static final String[] SMALL = new String[]{"", "m", "µ", "n"};
    private static final String[] LARGE = new String[]{"", "k", "M", "G"};
    private final double value;
    private String str;
    private String prefix;
    private String valStr;

    /**
     * Create a new magnify item
     *
     * @param value the mag value
     */
    public Magnify(double value) {
        this.value = value;
        setupValue(value);
    }

    /**
     * Initializes the string representation of this value
     *
     * @param value the value
     */
    void setupValue(double value) {
        double t = value;
        prefix = "";
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

        valStr = s;
        setString(valStr + prefix);
    }

    /**
     * Sets the string representation for this value
     *
     * @param str the string
     */
    void setString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    /**
     * @return the unit prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return the value in string form
     */
    public String getValStr() {
        return valStr;
    }

    /**
     * @return the mag value
     */
    public double getMag() {
        return value;
    }
}
