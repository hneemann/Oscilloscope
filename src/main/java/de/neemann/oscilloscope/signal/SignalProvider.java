package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.gui.Observer;

import java.util.ArrayList;

/**
 * A signal provider
 */
public class SignalProvider {
    private ArrayList<Observer> observers;
    private PeriodicSignal signal = PeriodicSignal.GND;

    /**
     * Sets a signal to this provider.
     * All observers are notified.
     *
     * @param signal the signal to set
     */
    public void setSignal(PeriodicSignal signal) {
        this.signal = signal;
        if (observers != null)
            for (Observer o : observers)
                o.hasChanged();
    }

    /**
     * @return the provided signal
     */
    public PeriodicSignal getSignal() {
        return signal;
    }

    /**
     * Adds an observer
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        if (observers == null)
            observers = new ArrayList<>();
        observers.add(observer);
    }

    /**
     * Removes the observer
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        if (observers != null)
            observers.remove(observer);
    }
}
