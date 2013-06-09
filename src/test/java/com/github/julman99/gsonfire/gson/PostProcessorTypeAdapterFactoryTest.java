package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.GsonFireBuilder;
import com.github.julman99.gsonfire.PostProcessor;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Assert;
import org.junit.Test;

/**
 * @autor: julio
 */
public class PostProcessorTypeAdapterFactoryTest {
    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .registerPostProcessor(A.class, new PostProcessor<A>() {
                    @Override
                    public void postDeserialize(A result, JsonElement src) {
                        result.aa = result.a + "2";
                    }

                    @Override
                    public void postSerialize(JsonElement result, A src) {
                        result.getAsJsonObject().addProperty("tmp", src.a);
                    }
                });
        Gson gson = builder.createGson();

        A a = new A();
        a.a = "xxxx";
        a.aa = "yyyy";

        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        Assert.assertEquals(json.get("a").getAsString(), a.a);
        Assert.assertEquals(json.get("tmp").getAsString(), a.a);
        Assert.assertEquals(json.get("aa").getAsString(), a.aa);

        A a2 = gson.fromJson(json, A.class);
        Assert.assertEquals(a2.a, a.a);
        Assert.assertEquals(a2.aa, a.a + "2");
    }

    private class A{
        public String a;
        public String aa;

    }

    private class B{

    }
}
