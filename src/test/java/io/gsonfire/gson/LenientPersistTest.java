package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PreProcessor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LenientPersistTest {

    @Test
    public void testLeninentPersist() {
        Gson gson = new GsonFireBuilder()
            .registerPreProcessor(SimpleThing.class, new PreProcessor<SimpleThing>() {
                @Override
                public void preDeserialize(Class<? extends SimpleThing> clazz, JsonElement src, Gson gson) {

                }
            })
            .createGsonBuilder()
            .setLenient()
            .create();

        String json = "{\"name\":\"bob\",\"value\":\"NaN\"} /*comment*/";
        SimpleThing result = gson.fromJson(json, SimpleThing.class);

        assertEquals(result.value, Double.NaN, 0);
    }

    @Test(expected = JsonSyntaxException.class)
    public void testNonLeninentPersist() {
        Gson gson = new GsonFireBuilder()
            .registerPreProcessor(SimpleThing.class, new PreProcessor<SimpleThing>() {
                @Override
                public void preDeserialize(Class<? extends SimpleThing> clazz, JsonElement src, Gson gson) {

                }
            })
            .createGsonBuilder()
            .create();

        String json = "{\"name\":\"bob\",\"value\":\"NaN\"} /*comment*/";
        SimpleThing result = gson.fromJson(json, SimpleThing.class);

        fail();
    }


    public static class SimpleThing {
        public String name;
        public double value;
    }

}
