package io.gsonfire.util;

/**
 * A functional interface for mapping values from one type to another.
 *
 * @param <F> The source type (from)
 * @param <T> The target type (to)
 * @author julio
 */
public interface Mapper<F,T> {

    /**
     * Maps a value from the source type to the target type.
     * @param from The source value
     * @return The mapped target value
     */
    T map(F from);

}
