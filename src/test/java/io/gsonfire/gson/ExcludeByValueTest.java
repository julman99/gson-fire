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
        a.str1 = "str1";
        a.b = new B();
        a.b.str1 = "str1";
        a.b.str2 = "str2";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals("str1", obj.get("str1").getAsString());
        assertEquals("str1", obj.get("b").getAsJsonObject().get("str1").getAsString());
        assertEquals("str2", obj.get("b").getAsJsonObject().get("str2").getAsString());
    }

    @Test
    public void testExclusion(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExclusionByValue();

        Gson gson = builder.createGson();

        A a = new A();
        a.str1 = "";
        a.b = new B();
        a.b.str1 = "";
        a.b.str2 = "";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertFalse(obj.has("str1"));
        assertTrue(obj.has("b"));
        assertFalse(obj.get("b").getAsJsonObject().has("str1"));
        assertEquals("", obj.get("b").getAsJsonObject().get("str2").getAsString());
    }


    private class A {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String str1;

        @Expose
        public B b;

    }

    private class B {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String str1;

        @Expose
        public String str2;

    }

    public static class ExcludeEmptyStringsStrategy implements ExclusionByValueStrategy<String> {

        @Override
        public boolean shouldSkipField(String fieldValue) {
            return "".equals(fieldValue);
        }

    }

}
