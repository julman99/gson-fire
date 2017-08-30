package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 3/25/17.
 */
public class GsonExtendTest {

    @Test
    public void testValueFallback() {
        Gson gsonDefault = new Gson();

        Gson gsonInner = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();


        Gson gsonOuter = new GsonFireBuilder()
            .extendGson(gsonInner)
            .createGson();

        long timestamp = 1490479024000L;
        Date date = new Date(timestamp);

        assertEquals(timestamp / 1000, gsonOuter.toJsonTree(date).getAsLong());
        assertEquals(timestamp / 1000, gsonInner.toJsonTree(date).getAsLong());
        assertEquals("Mar 25, 2017 5:57:04 PM", gsonDefault.toJsonTree(date).getAsString());
    }

    @Test
    public void testValueOverrideSerialization() {
        Gson gsonDefault = new Gson();

        Gson gsonInner = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();


        Gson gsonOuter = new GsonFireBuilder()
            .extendGson(gsonInner)
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeMillis)
            .createGson();

        long timestamp = 1490479024000L;
        Date date = new Date(timestamp);

        assertEquals(timestamp, gsonOuter.toJsonTree(date).getAsLong());
        assertEquals(timestamp / 1000, gsonInner.toJsonTree(date).getAsLong());
        assertEquals("Mar 25, 2017 5:57:04 PM", gsonDefault.toJsonTree(date).getAsString());
    }

    @Test
    public void testAClass() {
        Gson gsonDefault = new GsonFireBuilder()
            .registerPostProcessor(AClass.class, new PostProcessor<AClass>() {
                @Override
                public void postDeserialize(AClass result, JsonElement src, Gson gson) {

                }

                @Override
                public void postSerialize(JsonElement result, AClass src, Gson gson) {
                    int a = result.getAsJsonObject().get("a").getAsInt() + 1;
                    result.getAsJsonObject().addProperty("a", a);
                }
            })
            .createGson();

        Gson gsonOuter = new GsonFireBuilder()
            .extendGson(gsonDefault)
            .registerPostProcessor(AClass.class, new PostProcessor<AClass>() {
                @Override
                public void postDeserialize(AClass result, JsonElement src, Gson gson) {

                }

                @Override
                public void postSerialize(JsonElement result, AClass src, Gson gson) {
                    int b = result.getAsJsonObject().get("b").getAsInt() + 1;
                    result.getAsJsonObject().addProperty("b", b);
                }
            })
            .createGson();

        AClass aClass = new AClass();
        aClass.a = 1;
        aClass.b = 10;

        JsonObject jsonWithDefault = gsonDefault.toJsonTree(aClass).getAsJsonObject();
        JsonObject jsonWithOuter = gsonOuter.toJsonTree(aClass).getAsJsonObject();

        assertEquals(2, jsonWithDefault.get("a").getAsInt());
        assertEquals(10, jsonWithDefault.get("b").getAsInt());

        assertEquals(2, jsonWithOuter.get("a").getAsInt());
        assertEquals(11, jsonWithOuter.get("b").getAsInt());

        //Now test a class that should not be transformed

        OtherClass otherClass = new OtherClass();
        otherClass.a = 1;

        JsonObject json2WithDefault = gsonDefault.toJsonTree(otherClass).getAsJsonObject();
        JsonObject json2WithOuter = gsonOuter.toJsonTree(otherClass).getAsJsonObject();

        assertEquals(1, json2WithDefault.get("a").getAsInt());
        assertEquals(1, json2WithOuter.get("a").getAsInt());

    }

    @Test
    public void testObject() {

        final StringBuilder invocation = new StringBuilder();

        Gson gsonDefault = new GsonFireBuilder()
            .registerPostProcessor(Object.class, new PostProcessor<Object>() {
                @Override
                public void postDeserialize(Object result, JsonElement src, Gson gson) {

                }

                @Override
                public void postSerialize(JsonElement result, Object src, Gson gson) {
                    invocation.append("A");
                }
            })
            .createGson();

        Gson gsonOuter = new GsonFireBuilder()
            .extendGson(gsonDefault)
            .registerPostProcessor(Object.class, new PostProcessor<Object>() {
                @Override
                public void postDeserialize(Object result, JsonElement src, Gson gson) {

                }

                @Override
                public void postSerialize(JsonElement result, Object src, Gson gson) {
                    invocation.append("B");
                }
            })
            .createGson();


//        gsonOuter.toJsonTree("SOME OBJECT");
//        assertEquals("AB", invocation.toString());

        invocation.delete(0, invocation.length());

        gsonOuter.toJsonTree(new AClass());
        assertEquals("AAABBB", invocation.toString());
    }

    private static class AClass {

        public int a;
        public int b;

    }

    private static class OtherClass {

        public int a;

    }


}
