package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @autor: julio
 */
public class PostProcessorTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .registerPostProcessor(A.class, new PostProcessor<A>() {
                    @Override
                    public void postDeserialize(A result, JsonElement src, Gson gson) {
                        result.aa = result.a + "2";
                    }

                    @Override
                    public void postSerialize(JsonElement result, A src, Gson gson) {
                        result.getAsJsonObject().addProperty("tmp", src.a);
                    }
                })
                .registerPostProcessor(A.class, new PostProcessor<A>() {
                    @Override
                    public void postDeserialize(A result, JsonElement src, Gson gson) {
                        result.aa += "1";
                    }

                    @Override
                    public void postSerialize(JsonElement result, A src, Gson gson) {
                        result.getAsJsonObject().addProperty("tmp2", src.a);
                    }
                });
        Gson gson = builder.createGson();

        A a = new A();
        a.a = "xxxx";
        a.aa = "yyyy";

        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        Assert.assertEquals(json.get("a").getAsString(), a.a);
        Assert.assertEquals(json.get("tmp").getAsString(), a.a);
        Assert.assertEquals(json.get("tmp2").getAsString(), a.a);
        Assert.assertEquals(json.get("aa").getAsString(), a.aa);

        A a2 = gson.fromJson(json, A.class);
        Assert.assertEquals(a2.a, a.a);
        Assert.assertEquals(a2.aa, a.a + "21");
    }


    private class A{
        public String a;
        public String aa;

    }

    private class B{

    }
}
