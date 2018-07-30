package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
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
        a.c = new C();
        a.c.str1 = "x";
        a.c.str2 = "y";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals("str1", obj.get("str1").getAsString());
        assertEquals("str1", obj.get("b").getAsJsonObject().get("str1").getAsString());
        assertEquals("str2", obj.get("b").getAsJsonObject().get("str2").getAsString());
        assertEquals("x", obj.get("c").getAsJsonObject().get("x").getAsString());
        assertEquals("y", obj.get("c").getAsJsonObject().get("y").getAsString());
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
        a.c = new C();
        a.c.str1 = "";
        a.c.str2 = "";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertFalse(obj.has("str1"));
        assertTrue(obj.has("b"));
        assertFalse(obj.get("b").getAsJsonObject().has("str1"));
        assertEquals("", obj.get("b").getAsJsonObject().get("str2").getAsString());
        assertFalse(obj.get("c").getAsJsonObject().has("x"));
        assertFalse(obj.get("c").getAsJsonObject().has("str1"));
        assertEquals("", obj.get("c").getAsJsonObject().get("y").getAsString());

    }


    private class A {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String str1;

        @Expose
        public B b;

        @Expose
        public C c;

    }

    private class B {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        public String str1;

        @Expose
        public String str2;

    }

    private class C {

        @Expose
        @ExcludeByValue(ExcludeEmptyStringsStrategy.class)
        @SerializedName("x")
        public String str1;

        @Expose
        @SerializedName("y")
        public String str2;

    }

    public static class ExcludeEmptyStringsStrategy implements ExclusionByValueStrategy<String> {

        @Override
        public boolean shouldSkipField(String fieldValue) {
            return "".equals(fieldValue);
        }

    }

}
