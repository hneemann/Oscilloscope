package de.neemann.oscilloscope.draw.elements;

/**
 * A simple Onn/Off switch
 */
public class OnOffSwitch extends Switch<OnOffSwitch.OffOn> {

    protected enum OffOn {Off, On}

    /**
     * Creates a new switch
     *
     * @param name th name of the switch
     */
    public OnOffSwitch(String name) {
        super(name);
        add(OffOn.values());
    }

    /**
     * @return true if power if off
     */
    public boolean isOff() {
        return getSelected() == OffOn.Off;
    }

    /**
     * @return true if power if on
     */
    public boolean isOn() {
        return getSelected() == OffOn.On;
    }

}
