package de.neemann.oscilloscope.draw.elements;

/**
 * The visitor use to implement a visitor pattern
 */
public interface Visitor {
    /**
     * Called on every element
     *
     * @param element the element
     */
    void visit(Element<?> element);
}
