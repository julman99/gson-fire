package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import io.gsonfire.TypeSelector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NaNTest {

    public static Gson getGson(boolean triggerBug) {
        GsonFireBuilder builder = new GsonFireBuilder();
        if (triggerBug) {
            builder.registerPostProcessor(Buggy.class, new PostProcessor<Buggy>() {
                @Override
                public void postDeserialize(Buggy result, JsonElement src, Gson gson) {

                }

                @Override
                public void postSerialize(JsonElement result, Buggy src, Gson gson) {

                }
            });
            builder.registerTypeSelector(Object.class, new TypeSelector<Object>() {
                @Override
                public Class<?> getClassForElement(JsonElement readElement) {
                    return null;
                }
            });
        }
        GsonBuilder gb = builder.createGsonBuilder();
        gb.serializeSpecialFloatingPointValues();

        Gson gson = gb.create();
        return gson;
    }


    @Test
    public void testNan() {
        Buggy fm = new Buggy();
        Gson gson = getGson(true);
        String json = gson.toJson(fm);
        assertEquals("{\"a\":NaN}", json);
        Buggy to = gson.fromJson(json, Buggy.class);
        assertEquals(fm.a, to.a);
    }

    public static class Buggy {
        double a = Double.NaN;
    }
}
