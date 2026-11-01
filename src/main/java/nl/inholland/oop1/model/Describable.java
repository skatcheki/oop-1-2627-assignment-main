package nl.inholland.oop1.model;

/**
 * Something that can describe itself as a single line of text.
 */
public interface Describable {

    /**
     * Returns a one-line, human-readable description of this object.
     *
     * @return the description, never {@code null}
     */
    String describe();
}
