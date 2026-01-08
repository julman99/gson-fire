package io.gsonfire.util.reflection;

/**
 * A factory interface for creating or retrieving instances of a given class.
 *
 * @author julio
 */
public interface Factory {
    /**
     * Gets or creates an instance of the specified class.
     * @param clazz The class to instantiate
     * @param <T> The type of the class
     * @return An instance of the class
     */
    <T> T get(Class<T> clazz);
}
