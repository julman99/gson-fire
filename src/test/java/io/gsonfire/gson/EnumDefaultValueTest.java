package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import io.gsonfire.GsonFireBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 9/6/16.
 */
public class EnumDefaultValueTest {

    @Test
    public void testParse() {
        Gson gson = new GsonFireBuilder()
            .enumDefaultValue(MyEnum.class, MyEnum.other)
            .createGson();

        assertEquals(MyEnum.one, gson.fromJson(new JsonPrimitive("one"), MyEnum.class));
        assertEquals(MyEnum.two, gson.fromJson(new JsonPrimitive("two"), MyEnum.class));
        assertEquals(MyEnum.three, gson.fromJson(new JsonPrimitive("three"), MyEnum.class));
        assertEquals(MyEnum.other, gson.fromJson(new JsonPrimitive("four"), MyEnum.class));
    }

    @Test
    public void testParseToNull() {
        Gson gson = new GsonFireBuilder()
            .enumDefaultValue(MyEnum.class, null)
            .createGson();

        assertEquals(MyEnum.one, gson.fromJson(new JsonPrimitive("one"), MyEnum.class));
        assertEquals(MyEnum.two, gson.fromJson(new JsonPrimitive("two"), MyEnum.class));
        assertEquals(MyEnum.three, gson.fromJson(new JsonPrimitive("three"), MyEnum.class));
        assertEquals(null, gson.fromJson(new JsonPrimitive("four"), MyEnum.class));
    }

    @Test
    public void testSerialize() {
        Gson gsonWithSoftParse = new GsonFireBuilder()
            .enumDefaultValue(MyEnum.class, MyEnum.other)
            .createGson();


        Gson gsonNonSoftParse = new GsonFireBuilder()
            .createGson();

        assertEquals(gsonNonSoftParse.toJson(MyEnum.one), gsonWithSoftParse.toJson(MyEnum.one));
        assertEquals(gsonNonSoftParse.toJson(MyEnum.two), gsonWithSoftParse.toJson(MyEnum.two));
        assertEquals(gsonNonSoftParse.toJson(MyEnum.three), gsonWithSoftParse.toJson(MyEnum.three));
        assertEquals(gsonNonSoftParse.toJson(MyEnum.other), gsonWithSoftParse.toJson(MyEnum.other));
    }

    @Test
    public void testNull() {
        Gson gson = new GsonFireBuilder()
            .enumDefaultValue(MyEnum.class, MyEnum.other)
            .createGson();

        assertEquals(null, gson.fromJson(JsonNull.INSTANCE, MyEnum.class));
        assertEquals(JsonNull.INSTANCE, gson.toJsonTree(null, MyEnum.class));
    }

    public enum MyEnum {
        one, two, three, other
    }


}
