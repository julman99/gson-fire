package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.util.BasicIterable;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 6/23/15.
 */
public class BasicIterableTest {

    @Test
    public void testSimpleIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        BasicIterable<Integer> originalIterable = BasicIterable.of(1, 2, 3);
        String json = gson.toJson(originalIterable);
        assertEquals("[1,2,3]", json);

        BasicIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<BasicIterable<Integer>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testGenericIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        BasicIterable<GenericContainer<String>> originalIterable = BasicIterable.of(
            new GenericContainer<String>("a"),
            new GenericContainer<String>("b")
        );
        String json = gson.toJson(originalIterable);
        assertEquals("[{\"value\":\"a\"},{\"value\":\"b\"}]", json);

        BasicIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<BasicIterable<GenericContainer<String>>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testRecursiveGenericIterated() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        BasicIterable<GenericContainer<GenericContainer<Integer>>> originalIterable = BasicIterable.of(
            new GenericContainer<GenericContainer<Integer>>(new GenericContainer<Integer>(1)),
            new GenericContainer<GenericContainer<Integer>>(new GenericContainer<Integer>(2))
        );
        String json = gson.toJson(originalIterable);
        assertEquals("[{\"value\":{\"value\":1}},{\"value\":{\"value\":2}}]", json);

        BasicIterable<Integer> deserializedIterable = gson.fromJson(json, new TypeToken<BasicIterable<GenericContainer<GenericContainer<Integer>>>>(){}.getType());
        assertEquals(originalIterable, deserializedIterable);
    }

    @Test
    public void testBasicIterableContainer() {
        Gson gson = new GsonFireBuilder()
            .createGson();

        BasicIterableContainer container = new BasicIterableContainer(
            BasicIterable.of(1, 2, 3)
        );
        String json = gson.toJson(container);
        assertEquals("{\"value\":[1,2,3]}", json);

        BasicIterableContainer deserializedContainer = gson.fromJson(json, BasicIterableContainer.class);
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

    public static class BasicIterableContainer extends GenericContainer<BasicIterable<Integer>> {

        public BasicIterableContainer(BasicIterable<Integer> value) {
            super(value);
        }
    }

}
