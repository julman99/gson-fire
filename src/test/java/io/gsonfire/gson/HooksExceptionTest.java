package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.annotations.PreSerialize;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @autor: julio
 */
public class HooksExceptionTest {

    private static final ThreadLocal<Exception> EXCEPTION = new ThreadLocal<Exception>();

    @Test
    public void testPreSerializeException(){
        Gson gson = new GsonFireBuilder().enableHooks(A.class).createGson();
        EXCEPTION.set(new Exception());
        A a = new A();
        try {
            JsonObject json = gson.toJsonTree(a).getAsJsonObject();
            fail();
        } catch (HookInvocationException ex) {
            assertEquals(EXCEPTION.get(), ex.getCause());
        } catch (Throwable ex) {
            fail();
        }
    }

    @Test
    public void testPostDeserializeException(){
        EXCEPTION.set(new Exception());
        Gson gson = new GsonFireBuilder().enableHooks(A.class).createGson();
        try {
            A a = gson.fromJson(new JsonObject(), A.class);
            fail();
        } catch (HookInvocationException ex) {
            assertEquals(EXCEPTION.get(), ex.getCause());
        } catch (Throwable ex) {
            fail();
        }
    }

    @Test
    public void testNonEnabled(){
        Gson gson = new GsonFireBuilder().createGson();
        Exception expectedException = new Exception();
        A a = new A();
        JsonObject json = gson.toJsonTree(a).getAsJsonObject();
        assertEquals(0, json.get("count").getAsInt());

        A a2 = gson.fromJson(json, A.class);
        assertEquals(0, a.get());
    }

    private static class A {
        
        private int count;

        private A() {
        }

        @PreSerialize
        public void preSerialize() throws Exception {
            throw EXCEPTION.get();
        }

        @PostDeserialize
        public void postDeserialize() throws Exception {
            throw EXCEPTION.get();
        }

        public int get() {
            return count;
        }
    }

}
