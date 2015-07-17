package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.ExcludeByValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @autor: joao
 */
public class ExcludeByValueTest {

    @Test
    public void testNonExclusion(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExclusionByValue();

        Gson gson = builder.createGson();

        A a = new A();
        a.a = "a";
        a.b = new B();
        a.b.str = "str";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals(obj.get("a").getAsString(), "a");
        assertEquals(obj.get("b").getAsJsonObject().get("str").getAsString(), "str");
    }

    @Test
    public void testExclusion(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExclusionByValue();

        Gson gson = builder.createGson();

        A a = new A();
        a.a = "";
        a.b = new B();
        a.b.str = "";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertFalse(obj.has("a"));
        assertTrue(obj.has("b"));
        assertFalse(obj.get("b").getAsJsonObject().has("str"));
    }


    private class A {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String a;

        @Expose
        public B b;

    }

    private class B {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String str;

    }

    public static class ExcludeEmptyStringsStrategy implements ExclusionByValueStrategy<String> {

        @Override
        public boolean shouldSkipField(String fieldValue) {
            return "".equals(fieldValue);
        }

    }

}
