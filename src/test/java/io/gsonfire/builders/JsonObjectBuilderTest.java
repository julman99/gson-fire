package io.gsonfire.builders;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 8/18/16.
 */
public class JsonObjectBuilderTest {
    @Test
    public void setString() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .set("a", "aa")
            .set("b", "bb")
            .build();

        JsonObject expected = new JsonObject();
        expected.addProperty("a", "aa");
        expected.addProperty("b", "bb");

        assertEquals(expected, built);
    }

    @Test
    public void setNumber() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .set("a", 1)
            .set("b", 2f)
            .set("c", 3.1f)
            .set("d", 4L)
            .build();

        JsonObject expected = new JsonObject();
        expected.addProperty("a", 1);
        expected.addProperty("b", 2f);
        expected.addProperty("c", 3.1f);
        expected.addProperty("d", 4L);

        assertEquals(expected, built);
    }

    @Test
    public void setBoolean() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .set("a", true)
            .set("b", false)
            .build();

        JsonObject expected = new JsonObject();
        expected.addProperty("a", true);
        expected.addProperty("b", false);

        assertEquals(expected, built);
    }

    @Test
    public void setJsonElement() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .set("a", new JsonObjectBuilder().set("x", 1).build())
            .set("b", JsonArrayBuilder.start().add("something").build())
            .set("c", new JsonPrimitive(1))
            .build();

        JsonObject a = new JsonObject();
        a.addProperty("x", 1);

        JsonArray b = new JsonArray();
        b.add(new JsonPrimitive("something"));

        JsonObject expected = new JsonObject();
        expected.add("a", a);
        expected.add("b", b);
        expected.add("c", new JsonPrimitive(1));

        assertEquals(expected, built);
    }

    @Test
    public void setBuildImmutable() throws Exception {
        JsonObjectBuilder builder = new JsonObjectBuilder();
        JsonObject jsonObject1 = builder
            .set("a", 1)
            .build();

        JsonObject jsonObject2 = builder
            .set("b", 2)
            .build();

        JsonObject expected1 = new JsonObject();
        expected1.addProperty("a", 1);

        JsonObject expected2 = new JsonObject();
        expected2.addProperty("a", 1);
        expected2.addProperty("b", 2);

        assertEquals(expected1, jsonObject1);
        assertEquals(expected2, jsonObject2);
    }

    @Test
    public void setNull() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .setNull("a")
            .build();

        JsonObject expected = new JsonObject();
        expected.add("a", null);

        assertEquals(expected, built);
    }

    @Test
    public void merge() throws Exception {
        JsonObject built = new JsonObjectBuilder()
            .set("a", 1)
            .set("b", "b")
            .merge(new JsonObjectBuilder()
                .set("b", "b2")
                .set("c", true)
                .build()
            )
            .build();

        JsonObject expected = new JsonObject();
        expected.addProperty("a", 1);
        expected.addProperty("b", "b2");
        expected.addProperty("c", true);

        assertEquals(expected, built);
    }

}