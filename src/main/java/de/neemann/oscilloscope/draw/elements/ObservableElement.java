package de.neemann.oscilloscope.draw.elements;

import de.neemann.oscilloscope.gui.Observer;

import java.util.ArrayList;

/**
 * base class of observable elements
 *
 * @param <T> the concrete type
 */
public abstract class ObservableElement<T extends Element<?>> extends Element<T> {

    private ArrayList<Observer> observers;

    /**
     * Adds a observer ot the knob
     *
     * @param observer the observer
     */
    public void addObserver(Observer observer) {
        if (observers == null)
            observers = new ArrayList<>();
        observers.add(observer);
    }

    /**
     * call this method if the element has changed
     */
    public void hasChanged() {
        if (observers != null)
            for (Observer o : observers)
                o.hasChanged();
    }

}
