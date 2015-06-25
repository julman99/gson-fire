package io.gsonfire.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by julio on 6/24/15.
 */
public class IterableMapper<F, T> implements Iterable<T> {

    private final Iterable<F> source;
    private final Collection<T> cache = new ArrayList<T>();
    private final Mapper<F,T> mapper;

    private IterableMapper(Iterable<F> source, Mapper<F, T> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> cacheIterator = new ArrayList<T>(cache).iterator();
        final Iterator<F> sourceIterator = source.iterator();
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return cacheIterator.hasNext() || sourceIterator.hasNext();
            }

            @Override
            public T next() {
                if(cacheIterator.hasNext()) {
                    return cacheIterator.next();
                } else {
                    T mapped = mapper.map(sourceIterator.next());
                    cache.add(mapped);
                    return mapped;
                }
            }

            @Override
            public void remove() {

            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof Iterable) {
            Iterator otherIterator = ((Iterable) o).iterator();
            Iterator thisIterator = this.iterator();
            while(thisIterator.hasNext()) {
                if(otherIterator.hasNext()) {
                    Object thisNext = thisIterator.next();
                    Object otherNext = otherIterator.next();
                    if(!areObjectEquals(thisNext, otherNext)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            return !otherIterator.hasNext();
        } else {
            return false;
        }
    }

    private static boolean areObjectEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static <F,T> Iterable<T> create(Iterable<F> source, Mapper<F, T> mapper) {
        return new IterableMapper<F, T>(source, mapper);
    }

}
