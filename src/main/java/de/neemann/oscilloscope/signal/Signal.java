package de.neemann.oscilloscope.signal;

import de.neemann.oscilloscope.gui.Observer;

import java.util.ArrayList;

/**
 * A signal
 */
public abstract class Signal implements Observer {
    private ArrayList<Observer> observerList;

    /**
     * The signal value
     *
     * @param t the time
     * @return the value
     */
    public abstract double v(double t);

    @Override
    public void hasChanged() {
        if (observerList != null)
            for (Observer o : observerList)
                o.hasChanged();
    }

    /**
     * Adds a observer to this signal.
     *
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        if (observerList == null)
            observerList = new ArrayList<>();
        observerList.add(observer);
    }

    /**
     * Removes a observer from this signal.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(Observer observer) {
        if (observerList == null)
            return;
        observerList.remove(observer);
        if (observerList.isEmpty())
            observerList = null;
    }

}
