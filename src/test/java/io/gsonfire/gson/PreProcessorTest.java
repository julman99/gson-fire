package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PreProcessor;
import org.junit.Assert;
import org.junit.Test;

/**
 * @autor: julio
 */
public class PreProcessorTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .registerPreProcessor(A.class, new PreProcessor<A>() {
                    @Override
                    public void preDeserialize(Class<? extends A> clazz, JsonElement src, Gson gson) {
                        src.getAsJsonObject().addProperty("a", "changed");
                    }
                });

        Gson gson = builder.createGson();

        A a = new A();
        a.a = "a";
        a.b = "b";


        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        Assert.assertEquals(json.get("a").getAsString(), a.a);
        Assert.assertEquals(json.get("b").getAsString(), a.b);

        A a2 = gson.fromJson(json, A.class);
        Assert.assertEquals("changed", a2.a);
        Assert.assertEquals(a.b, a2.b);
    }


    private class A {
        public String a;
        public String b;

    }

    private class B{

    }
}
