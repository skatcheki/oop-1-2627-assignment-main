package nl.inholland.oop1.model;

/**
 * Decides whether an object should be selected, for example by {@link SmartHome#find(Selector)}.
 *
 * @param <T> the type of object that is tested
 */
@FunctionalInterface
public interface Selector<T> {

    /**
     * Tests the given object.
     *
     * @param t the object to test
     * @return {@code true} if the object is selected, {@code false} otherwise
     */
    boolean select(T t);
}
