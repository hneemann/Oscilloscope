package de.neemann.oscilloscope.exercises;

import de.neemann.oscilloscope.draw.elements.Container;
import de.neemann.oscilloscope.draw.elements.Element;
import de.neemann.oscilloscope.draw.elements.Visitor;
import de.neemann.oscilloscope.draw.elements.generator.Generator;
import de.neemann.oscilloscope.draw.elements.osco.Oscilloscope;
import de.neemann.oscilloscope.gui.ElementComponent;

/**
 * Implements a certain exercise.
 */
public interface Exercise {

    /**
     * Creates the exercise
     *
     * @return the created container
     */
    Container<?> create();

    /**
     * Brings the exercise to a working state
     *
     * @param component the {@link ElementComponent}
     */
    default void setup(ElementComponent component) {
    }

    /**
     * Returns the given oscilloscope
     *
     * @param container the {@link ElementComponent}
     * @return the oscilloscope or null if not found
     */
    static Oscilloscope getOscilloscope(ElementComponent container) {
        return get(container, e -> e instanceof Oscilloscope);
    }

    /**
     * Returns the given generator
     *
     * @param container the {@link ElementComponent}
     * @param name      the name of the generator
     * @return the generator or null if not found
     */
    static Generator getGenerator(ElementComponent container, String name) {
        return get(container, e -> e instanceof Generator && ((Generator) e).getName().equals(name));
    }

    /**
     * Picks a certain Element from the given {@link ElementComponent}
     *
     * @param elementComponent the {@link ElementComponent}
     * @param m                the matcher to find the {@link Element}
     * @param <T>              the type of the element
     * @return the found element of null if not found
     */
    static <T extends Element<T>> T get(ElementComponent elementComponent, Match m) {
        Filter filter = new Filter(m);
        elementComponent.traverse(filter);
        return (T) filter.get();
    }

    /**
     * A element filter
     */
    class Filter implements Visitor {
        private final Match m;
        private Element<?> found;

        public Filter(Match m) {
            this.m = m;
        }

        @Override
        public void visit(Element<?> element) {
            if (m.match(element))
                found = element;
        }

        public Element<?> get() {
            return found;
        }
    }

    /**
     * A simple matcher
     */
    interface Match {
        /**
         * Returns true if the given element e matches
         *
         * @param e the element
         * @return true if the given element e matches
         */
        boolean match(Element<?> e);
    }

}
