package de.neemann.oscilloscope.draw.elements.osco;

/**
 * The trigger slope
 */
public enum Slope {
    /**
     * rising edge
     */
    up("↑"),
    /**
     * falling edge
     */
    down("↓");

    private final String name;

    Slope(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
