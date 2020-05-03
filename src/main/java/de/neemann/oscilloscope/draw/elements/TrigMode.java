package de.neemann.oscilloscope.draw.elements;

/**
 * The trigger mode
 */
public enum TrigMode {
    /**
     * Normal trigger
     */
    NORM,
    /**
     * AUTO trigger
     */
    AUTO,
    /**
     * TV vertical
     */
    TV_V,
    /**
     * TV horizontal
     */
    TV_H;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', '-');
        return name;
    }
}
