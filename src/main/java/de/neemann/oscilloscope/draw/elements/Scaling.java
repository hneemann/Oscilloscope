package de.neemann.oscilloscope.draw.elements;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * Handles the scaling of the oscilloscope
 */
public final class Scaling {
    private static final Preferences PREFS = Preferences.userRoot().node("oscilloscope");
    private static final String PREF_SIZE = "size";

    private Scaling() {
    }

    /**
     * internal grid size
     */
    public static final int SIZE;

    /**
     * half the internal grid size
     */
    public static final int SIZE2;

    static {
        int s = Scaling.PREFS.getInt(PREF_SIZE, 0);
        if (s < 10) {
            s = 20;
            try {
                if (Toolkit.getDefaultToolkit().getScreenSize().getHeight() < 900) {
                    s = 16;
                }
            } catch (Exception e) {
                // use 20 if there was an exception
            }
        }
        SIZE2 = s / 2;
        SIZE = SIZE2 * 2;
    }

    /**
     * Sets the default scaling
     *
     * @param s the scaling value int the range of 12 to 20
     */
    public static void setDefault(int s) {
        Scaling.PREFS.putInt(PREF_SIZE, s);
    }
}
