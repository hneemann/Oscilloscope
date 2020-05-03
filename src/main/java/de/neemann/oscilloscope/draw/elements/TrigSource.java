package de.neemann.oscilloscope.draw.elements;

/**
 * the trigger source
 */
public enum TrigSource {
    /**
     * Trigger on channel 1
     */
    Ch_1,
    /**
     * Trigger on channel 2
     */
    Ch_2,
    /**
     * Trigger on mains
     */
    LINE,
    /**
     * Trigger on an external signal
     */
    EXT;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', ' ');
        return name;
    }

}
