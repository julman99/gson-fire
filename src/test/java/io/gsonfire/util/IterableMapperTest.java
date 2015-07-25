package io.gsonfire.util;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 6/24/15.
 */
public class IterableMapperTest {

    @Test
    public void testMapsCorrectly() throws Exception {
        Iterable<Integer> integerIterable = SimpleIterable.of(1, 2, 3);
        Iterable<String> iterableMapper = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );
        Collection<String> expected = new ArrayList<String>(Arrays.asList("1", "2", "3"));
        assertEquals(expected, toCollection(iterableMapper));
    }

    @Test
    public void testMapsWhenIterates() throws Exception {
        Iterable<Integer> integerIterable = SimpleIterable.of(1, 2, 3);
        CountedMapper<Integer, String> counterMapper = new CountedMapper<Integer, String>(new IntegerToStringMapper());
        Iterable<String> iterableMapper = IterableMapper.create(
            integerIterable,
            counterMapper
        );

        int mapCount = 0;
        int itemCount = 0;

        Iterator<String> iterator1 = iterableMapper.iterator();

        while(iterator1.hasNext()) {
            assertEquals(mapCount, counterMapper.getMapCount());
            iterator1.next();
            mapCount++;
            itemCount++;
            assertEquals(mapCount, counterMapper.getMapCount());
            Iterator<String> iterator2 = iterableMapper.iterator();
            for(int i=0; i<itemCount; i++) {
                iterator2.next();
                mapCount++;
            }

            assertEquals(mapCount, counterMapper.getMapCount());
        }
    }

    @Test
    public void testEqualsWithValues() throws Exception {
        Iterable<Integer> integerIterable = SimpleIterable.of(1, 2, 3);
        Iterable<String> iterableMapper1 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );
        Iterable<String> iterableMapper2 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );

        assertEquals(iterableMapper1, iterableMapper2);
    }

    @Test
    public void testEqualsEmpty() throws Exception {
        Iterable<Integer> integerIterable = SimpleIterable.of();
        Iterable<String> iterableMapper1 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );
        Iterable<String> iterableMapper2 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );

        assertEquals(iterableMapper1, iterableMapper2);
    }

    @Test
    public void testEqualsWithNull() throws Exception {
        Iterable<Integer> integerIterable = SimpleIterable.of((Integer)null, 1);
        Iterable<String> iterableMapper1 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );
        Iterable<String> iterableMapper2 = IterableMapper.create(
            integerIterable,
            new IntegerToStringMapper()
        );

        assertEquals(iterableMapper1, iterableMapper2);
    }

    private static <T> Collection<T> toCollection(Iterable<T> iterable) {
        List<T> list = new ArrayList<T>();
        for(T v: iterable) {
            list.add(v);
        }
        return list;
    }

    private static class IntegerToStringMapper implements Mapper<Integer, String> {

        @Override
        public String map(Integer from) {
            if(from == null) {
                return null;
            } else {
                return from.toString();
            }
        }

    }

    private static class CountedMapper<F, T> implements Mapper<F, T> {

        private final Mapper<F, T> mapper;

        private int mapCount = 0;

        private CountedMapper(Mapper<F, T> mapper) {
            this.mapper = mapper;
        }

        @Override
        public T map(F from) {
            mapCount++;
            return mapper.map(from);
        }

        public int getMapCount() {
            return mapCount;
        }
    }
}