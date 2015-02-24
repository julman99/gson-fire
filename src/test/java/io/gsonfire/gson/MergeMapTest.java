package io.gsonfire.gson;

import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.MergeMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


/**
 * @autor: julio
 */
public class MergeMapTest {

    @Test
    public void testMergeMap(){
        Gson gson = new GsonFireBuilder()
                .enableMergeMaps(A.class)
                .createGsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        A a = new A();
        a.a = "hello";
        a.b.put("name", "john");
        a.b.put("age", 21);
        a.c.put("ignored", "ignored");

        JsonObject json = gson.toJsonTree(a).getAsJsonObject();

        assertEquals("hello", json.get("a").getAsString());
        assertEquals("john", json.get("name").getAsString());
        assertEquals(21, json.get("age").getAsInt());
        assertFalse(json.has("ignored"));
    }

    private class A{

        @Expose
        public String a;

        @MergeMap
        public Map<String, Object> b = new HashMap<String, Object>();

        public Map<String, Object> c = new HashMap<String, Object>();
    }
}
