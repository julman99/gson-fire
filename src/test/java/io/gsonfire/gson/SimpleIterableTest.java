package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.util.SimpleIterable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 6/23/15.
 */
public class SimpleIterableTest {

    @Test
    public void testSimpleIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        SimpleIterable<Integer> originalIterable = SimpleIterable.of(1, 2, 3);
        String json = gson.toJson(originalIterable);
        assertEquals("[1,2,3]", json);

        SimpleIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<SimpleIterable<Integer>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testGenericIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        SimpleIterable<GenericContainer<String>> originalIterable = SimpleIterable.of(
            new GenericContainer<String>("a"),
            new GenericContainer<String>("b")
        );
        String json = gson.toJson(originalIterable);
        assertEquals("[{\"value\":\"a\"},{\"value\":\"b\"}]", json);

        SimpleIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<SimpleIterable<GenericContainer<String>>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testRecursiveGenericIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        SimpleIterable<GenericContainer<GenericContainer<Integer>>> originalIterable = SimpleIterable.of(
            new GenericContainer<GenericContainer<Integer>>(new GenericContainer<Integer>(1)),
            new GenericContainer<GenericContainer<Integer>>(new GenericContainer<Integer>(2))
        );
        String json = gson.toJson(originalIterable);
        assertEquals("[{\"value\":{\"value\":1}},{\"value\":{\"value\":2}}]", json);

        SimpleIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<SimpleIterable<GenericContainer<GenericContainer<Integer>>>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testSimpleIterableContainer() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        SimpleIterableContainer container = new SimpleIterableContainer(
            SimpleIterable.of(1, 2, 3)
        );
        String json = gson.toJson(container);
        assertEquals("{\"value\":[1,2,3]}", json);

        SimpleIterableContainer deserializedContainer = gson.fromJson(json, SimpleIterableContainer.class);
        assertEquals(container, deserializedContainer);
    }

    public static class GenericContainer<T> {
        public final T value;

        public GenericContainer(T value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GenericContainer<?> that = (GenericContainer<?>) o;

            return !(value != null ? !value.equals(that.value) : that.value != null);

        }

        @Override
        public int hashCode() {
            return value != null ? value.hashCode() : 0;
        }
    }

    public static class SimpleIterableContainer extends GenericContainer<SimpleIterable<Integer>> {

        public SimpleIterableContainer(SimpleIterable<Integer> value) {
            super(value);
        }
    }

}
