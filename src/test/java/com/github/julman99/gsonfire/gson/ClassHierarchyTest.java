package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.GsonFireBuilder;
import com.github.julman99.gsonfire.PostProcessor;
import com.github.julman99.gsonfire.annotations.ExposeMethodResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 11/28/14.
 */
public class ClassHierarchyTest {

    @Test
    public void test() {
        A aa = new AA();
        A bb = new BB();

        Gson gson = new GsonFireBuilder()
            .registerPostProcessor(BB.class, new ValueLowerCasePostProcessor())
            .enableExposeMethodResult()
            .registerPostProcessor(AA.class, new ValueLowerCasePostProcessor())
            .createGson();

        assertEquals(aa.value().toLowerCase(), gson.toJsonTree(aa).getAsJsonObject().get("value_lower").getAsString());
        assertEquals(bb.value().toLowerCase(), gson.toJsonTree(bb).getAsJsonObject().get("value_lower").getAsString());
    }


    static interface A {

        String value();

    }

    static class AA implements A {

        @Override
        @ExposeMethodResult("value")
        public String value() {
            return "AA";
        }

        @Override
        public int hashCode() {
            return Integer.MIN_VALUE;
        }

    }

    static class BB implements A {

        @Override
        @ExposeMethodResult("value")
        public String value() {
            return "BB";
        }

        @Override
        public int hashCode() {
            return Integer.MAX_VALUE;
        }

    }

    static class ValueLowerCasePostProcessor implements PostProcessor<A> {

        @Override
        public void postDeserialize(A result, JsonElement src, Gson gson) {

        }

        @Override
        public void postSerialize(JsonElement result, A src, Gson gson) {
            result.getAsJsonObject().addProperty("value_lower", result.getAsJsonObject().get("value").getAsString().toLowerCase());
        }
    }

}
