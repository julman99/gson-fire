package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.ExposeMethodResult;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @autor: julio
 */
public class ExposeMethodResultTest {

    @Test
    public void test(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExposeMethodResult();

        Gson gson = builder.createGson();

        B a = new B();
        a.a = "a";
        a.b = "b";

        JsonObject obj = gson.toJsonTree(a).getAsJsonObject();

        assertEquals(obj.get("a").getAsString(), "a");
        assertEquals(obj.get("pub").getAsString(), "a-pub");
        assertEquals(obj.get("pro").getAsString(), "a-pro");
        assertEquals(obj.get("pri").getAsString(), "a-pri");
        assertEquals(obj.get("b").getAsString(), "b");
        assertEquals(obj.get("pub2").getAsString(), "b-pub2");
        assertEquals(obj.get("pro2").getAsString(), "b-pro2");
        assertEquals(obj.get("pri2").getAsString(), "b-pri2");
    }

    @Test
    public void testError(){
        GsonFireBuilder builder = new GsonFireBuilder()
                .enableExposeMethodResult();

        Gson gson = builder.createGson();

        ForError a = new ForError();

        try {
            JsonObject obj = gson.toJsonTree(a).getAsJsonObject();
            fail();
        } catch (IllegalArgumentException ex){
            assertTrue(true);
        }
    }

    @Test
    public void testConflictResolution(){
        GsonFireBuilder builder = new GsonFireBuilder()
            .enableExposeMethodResult();

        Gson gson = builder.createGson();

        ForConflict c = new ForConflict();
        c.a = "A";
        c.b = "B";

        JsonObject obj = gson.toJsonTree(c).getAsJsonObject();

        assertEquals(c.getA(), obj.get("a").getAsString());
        assertEquals(c.b, obj.get("b").getAsString());
        assertEquals(c.getC(), obj.get("c").getAsString());
        assertEquals(c.getD(), obj.get("d").getAsString());
    }

    @Test
    public void testInterfaceMethodsMapping(){
        GsonFireBuilder builder = new GsonFireBuilder()
            .enableExposeMethodResult();

        Gson gson = builder.createGson();

        InterfaceTest.Impl impl = new InterfaceTest.Impl();


        JsonObject obj = gson.toJsonTree(impl).getAsJsonObject();

        assertEquals(impl.a(), obj.get("a").getAsString());
        assertEquals(impl.b(), obj.get("b").getAsString());
    }


    private class A{
        public String a;

        @ExposeMethodResult("pub")
        public String pub(){
            return a + "-pub";
        }

        @ExposeMethodResult("pro")
        protected String pro(){
            return a + "-pro";
        }

        @ExposeMethodResult("pri")
        private String pri(){
            return a + "-pri";
        }
    }

    private class B extends A{
        public String b;

        @ExposeMethodResult("pub2")
        public String pub2(){
            return b + "-pub2";
        }

        @ExposeMethodResult("pro2")
        protected String pro2(){
            return b + "-pro2";
        }

        @ExposeMethodResult("pri2")
        private String pri2(){
            return b + "-pri2";
        }
    }

    private class ForError{

        @ExposeMethodResult("error")
        public String error(int a){
            return "error";
        }

    }

    private class ForConflict{
        public String a;
        public String b;

        @ExposeMethodResult(value = "a", conflictResolution = ExposeMethodResult.ConflictResolutionStrategy.OVERWRITE)
        public String getA(){
            return a + "_method";
        }

        @ExposeMethodResult(value = "b", conflictResolution = ExposeMethodResult.ConflictResolutionStrategy.SKIP)
        public String getB(){
            return b + "_method";
        }

        @ExposeMethodResult(value = "c", conflictResolution = ExposeMethodResult.ConflictResolutionStrategy.OVERWRITE)
        public String getC(){
            return "c_method";
        }

        @ExposeMethodResult(value = "d", conflictResolution = ExposeMethodResult.ConflictResolutionStrategy.SKIP)
        public String getD(){
            return "d_method";
        }
    }

    private static class InterfaceTest{
        public interface Interface{
            @ExposeMethodResult("a")
            String a();

            String b();
        }
        public static class Impl implements Interface{

            @Override
            public String a() {
                return "a";
            }

            @Override
            @ExposeMethodResult("b")
            public String b() {
                return "b";
            }
        }
    }
}
