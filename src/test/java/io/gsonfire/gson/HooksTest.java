package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.annotations.PreSerialize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class HooksTest {

    @Test
    public void testPreSerialize(){
        Gson gson = new GsonFireBuilder().enableHooks(A.class).createGson();
        A a = new A(0);
        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        assertEquals(1, json.get("count").getAsInt());
    }

    @Test
    public void testPostDeserialize(){

        JsonObject json = new JsonObject();
        json.addProperty("count", 10);
        json.addProperty("nonSerializable", 20);

        Gson gson = new GsonFireBuilder().enableHooks(A.class).createGson();
        A a = gson.fromJson(json, A.class);

        assertEquals(20, a.getNonSerializable());
        assertEquals(9, a.get());
        assertEquals(gson, a.getGson());
    }

    @Test
    public void testRecursion() {
        B b = new B(0);
        b.b = new B(10);

        Gson gson = new GsonFireBuilder().enableHooks(B.class).createGson();
        JsonObject bjson = gson.toJsonTree(b).getAsJsonObject();

        assertEquals(1, bjson.get("count").getAsInt());
        assertEquals(11, bjson.get("b").getAsJsonObject().get("count").getAsInt());

        B b2 = gson.fromJson(bjson, B.class);
        assertEquals(0, b2.get());
        assertEquals(10, b2.b.get());
    }

    @Test
    public void testNull(){
        Gson gson = new GsonFireBuilder().enableHooks(A.class).createGson();
        JsonElement json = gson.toJsonTree(null, A.class);
        assertTrue(json.isJsonNull());
    }

    @Test
    public void testNonEnabled(){
        Gson gson = new GsonFireBuilder().createGson();

        A a = new A(0);
        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        assertEquals(0, json.get("count").getAsInt());

        A a2 = gson.fromJson(json, A.class);
        assertEquals(0, a.get());
    }

    private class A{
        private int count = 0;
        private transient int nonSerializable = -1;
        private Gson gson;

        private A(int count) {
            this.count = count;
        }

        @PreSerialize
        public void inc(){
            count++;
        }

        @PostDeserialize
        public void dec(){
            count--;
        }

        @PostDeserialize
        public void setNonSerializable(Gson gson, JsonElement jsonElement){
            JsonElement nonSerializable = jsonElement.getAsJsonObject().get("nonSerializable");
            this.nonSerializable = nonSerializable == null ? -1 : nonSerializable.getAsInt();
            this.gson = gson;
        }

        public int get(){
            return count;
        }

        public int getNonSerializable() {
            return nonSerializable;
        }

        public Gson getGson() {
            return gson;
        }
    }

    private class B extends A {
        public B b;

        private B(int count) {
            super(count);
        }
    }
}
