package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.StringSerializer;
import io.gsonfire.builders.JsonObjectBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapKeyTest {

    @Test
    public void testTypedMap() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .create();
        
        map.put(new A("key"), "val");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("val", res.getAsJsonObject().get("key").getAsString());
    }

    @Test
    public void testUntypedMap() {
        Map<Object, Object> map = new HashMap<Object, Object>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .create();

        map.put(new A("key"), "val");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("val", res.getAsJsonObject().get("key").getAsString());
    }

    @Test
    public void testSubClass() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .create();

        map.put(new AA("key"), "val");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("val", res.getAsJsonObject().get("key").getAsString());
    }

    @Test
    public void testNoRegisteredMapKeySerializer() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            //.registerMapKeySerializer(A.class, new MapKeyTest.ASerializer()) *** TESTING THIS ON PURPOSE ***
            .createGsonBuilder()
            .create();

        AA aa = new AA("key");
        map.put(aa, "val");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("val", res.getAsJsonObject().get(aa.toString()).getAsString());
    }

    @Test
    public void testValueSameAsKey() {
        Map<A, A> map = new HashMap<A, A>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .create();

        A aa = new A("key");
        A aav = new A("value");
        map.put(aa, aav);

        JsonObject expected = new JsonObjectBuilder()
            .set("key", gson.toJsonTree(aav))
            .build();

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals(expected, res);
    }

    @Test
    public void testNullValue() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .serializeNulls()
            .create();

        map.put(new A("key"), null);

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals(true, res.getAsJsonObject().get("key").isJsonNull());
    }

    @Test
    public void testNullKey() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            .registerMapKeySerializer(A.class, new MapKeyTest.ASerializer())
            .createGsonBuilder()
            .serializeNulls()
            .create();

        map.put(null, "value");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("value", res.getAsJsonObject().get("null").getAsString());
    }

    @Test
    public void testNullKeyOnUnmodifiedGson() {
        Map<A, String> map = new HashMap<A, String>();

        Gson gson = new GsonFireBuilder()
            .createGsonBuilder()
            .serializeNulls()
            .create();

        map.put(null, "value");

        JsonElement res = gson.toJsonTree(map);
        assertEquals(1, res.getAsJsonObject().size());
        assertEquals("value", res.getAsJsonObject().get("null").getAsString());
    }

    public static class A {
        String a;

        public A(String a) {
            this.a = a;
        }
    }

    public static class AA extends A {
        public AA(String key) {
            super(key);
        }
    }

    public static class ASerializer extends StringSerializer<A> {
        @Override
        public String toString(A from) {
            return from.a;
        }

        @Override
        public A fromString(String s) {
            return new A(s);
        }
    }
}
