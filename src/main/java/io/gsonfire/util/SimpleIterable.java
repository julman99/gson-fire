package io.gsonfire.util;

import java.util.*;

/**
 * Created by julio on 6/23/15.
 */
public final class SimpleIterable<T> implements Iterable<T>{

    private final Iterable<T> iterable;

    private SimpleIterable(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public Iterator<T> iterator() {
        return iterable.iterator();
    }

    /**
     * Copies the iterable into a new collection
     * @return A new collection with the full content of the iterable
     */
    public final Collection<T> toCollection() {
        List<T> list = new ArrayList<T>();
        addTo(list);
        return list;
    }

    /**
     * Adds all elements from this iterable to the specified collection.
     * @param collection The collection to add elements to
     */
    private void addTo(Collection<T> collection) {
        for(T v: this) {
            collection.add(v);
        }
    }

    /**
     * Creates a {@link SimpleIterable} that iterates through the contents of the iterable passed as argument
     * @param iterable iterable
     * @param <T> type
     * @return {@link SimpleIterable}
     */
    public static <T> SimpleIterable<T> of(Iterable<T> iterable) {
        if(iterable == null) {
            throw new NullPointerException("The iterable parameter cannot be null");
        } else {
            return new SimpleIterable<T>(iterable);
        }
    }

    /**
     * Creates a {@link SimpleIterable} that iterates through the contents of the array passed as argument
     * @param array array
     * @param <T> type
     * @return {@link SimpleIterable}
     */
    public static <T> SimpleIterable<T> of(T... array) {
        return of(Arrays.asList(array));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleIterable<?> that = (SimpleIterable<?>) o;

        return !(iterable != null ? !iterable.equals(that.iterable) : that.iterable != null);

    }

    @Override
    public int hashCode() {
        return iterable != null ? iterable.hashCode() : 0;
    }

    @Override
    public String toString() {
        return iterable.toString();
    }

}
