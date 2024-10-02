package de.legoshi.util;

public interface Copyable<T> {

    /**
     * @return A deep copy of the object
     */
    T copy();
}
