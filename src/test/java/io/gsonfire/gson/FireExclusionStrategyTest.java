package io.gsonfire.gson;

import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.ExposeMethodResult;
import io.gsonfire.postprocessors.MappedMethod;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by julio on 5/25/15.
 */
public class FireExclusionStrategyTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
            .enableExposeMethodResult()
            .addSerializationExclusionStrategy(new FireExclusionStrategy() {
                @Override
                public boolean shouldSkipMethod(MappedMethod method) {
                    return method.getSerializedName().equals("excluded");
                }

                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            });

        Gson gson = builder.createGson();

        A a = new A();
        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals(1, obj.entrySet().size());
        assertFalse(obj.has("excluded"));
        assertEquals("included", obj.get("included").getAsString());
    }

    private class A {

        @ExposeMethodResult("included")
        public String included(){
            return "included";
        }

        @ExposeMethodResult("excluded")
        public String excluded(){
            return "excluded";
        }

    }

}
