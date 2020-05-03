package de.neemann.oscilloscope.draw.elements;

public enum Mode {
    Ch_1, Ch_2, DUAL, ADD;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', ' ');
        return name;
    }
}
