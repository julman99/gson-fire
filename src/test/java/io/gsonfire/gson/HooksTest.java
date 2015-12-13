package io.gsonfire.gson;

import com.google.gson.JsonElement;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.annotations.PreSerialize;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
            this.nonSerializable = jsonElement.getAsJsonObject().get("nonSerializable").getAsInt();
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
}
