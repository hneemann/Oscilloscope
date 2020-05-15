package de.neemann.oscilloscope.gui;

/**
 * Used to provide a global debug flag
 */
public final class Debug {

    private static boolean debug = false;

    private Debug() {
    }

    /**
     * Set the debug flag
     */
    public static void setDebug() {
        debug = true;
    }

    /**
     * @return true if in debug mode
     */
    public static boolean isDebug() {
        return debug;
    }
}
