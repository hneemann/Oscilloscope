package de.neemann.oscilloscope.draw.elements;

/**
 * The main oscilloscope mode
 */
public enum Mode {
    /**
     * show channel 1
     */
    Ch_1,
    /**
     * show channel 1
     */
    Ch_2,
    /**
     * show both channels
     */
    DUAL,
    /**
     * adds both channels
     */
    ADD;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', ' ');
        return name;
    }
}
