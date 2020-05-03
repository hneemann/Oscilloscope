package de.neemann.oscilloscope.draw.elements;

public enum TrigMode {
    NORM, AUTO, TV_V, TV_H;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', '-');
        return name;
    }
}
