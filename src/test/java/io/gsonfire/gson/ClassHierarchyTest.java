package io.gsonfire.gson;

import com.google.gson.JsonObject;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;
import io.gsonfire.TypeSelector;
import io.gsonfire.annotations.ExposeMethodResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import io.gsonfire.annotations.PostDeserialize;
import io.gsonfire.builders.JsonObjectBuilder;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void testTypeSelectorAndHierarchy() {
        final AtomicInteger typeSelectorCount = new AtomicInteger(0);
        JsonObject jsonObject = new JsonObjectBuilder()
            .set("value", "A VALUE")
            .build();

        Gson gson = new GsonFireBuilder()
            .enableHooks(CC.class)
            .registerTypeSelector(A.class, new TypeSelector<A>() {
                @Override
                public Class<? extends A> getClassForElement(JsonElement readElement) {
                    typeSelectorCount.incrementAndGet();
                    return CC.class;
                }
            })
            .registerPostProcessor(CC.class, new ValueLowerCasePostProcessorCC())
            .createGson();

        CC aa = (CC) gson.fromJson(jsonObject, A.class);

        assertEquals("a value", aa.value());
        assertEquals(1, aa.autoIncremented);
        assertEquals(1, typeSelectorCount.get());
    }


    interface A {

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

    static class CC implements A {

        public String value;
        public int autoIncremented = 0;

        @Override
        public String value() {
            return value;
        }

        @PostDeserialize
        public void inc() {
            autoIncremented++;
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

    static class ValueLowerCasePostProcessorCC implements PostProcessor<CC> {

        @Override
        public void postDeserialize(CC result, JsonElement src, Gson gson) {
            result.value = result.value.toLowerCase();
        }

        @Override
        public void postSerialize(JsonElement result, CC src, Gson gson) {
            result.getAsJsonObject().addProperty("value_lower", result.getAsJsonObject().get("value").getAsString().toLowerCase());
        }
    }

}
