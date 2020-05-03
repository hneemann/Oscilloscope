package de.neemann.oscilloscope.draw.elements;

public enum TrigSource {
    Ch_1, Ch_2, LINE, EXT;

    private String name;

    @Override
    public String toString() {
        if (name == null)
            name = super.toString().replace('_', ' ');
        return name;
    }

}
