package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.DateSerializationPolicy;
import com.github.julman99.gsonfire.GsonFireBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @autor: julio
 */
public class DateSerializationTest {

    @Test
    public void testUnixTimestampSeconds_serialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();

        final Date date = new Date();
        JsonElement element = gson.toJsonTree(date);

        assertEquals(date.getTime() / 1000L, element.getAsLong());
    }

    @Test
    public void testUnixTimestampSeconds_deserialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();

        long timeNoMillis = System.currentTimeMillis() / 1000L;
        JsonElement element = new JsonPrimitive(timeNoMillis);
        Date parsedDate = gson.fromJson(element, Date.class);

        assertEquals(timeNoMillis * 1000, parsedDate.getTime());
    }

    @Test
    public void testUnixTimestampMillis_serialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeMillis)
            .createGson();

        final Date date = new Date();
        JsonElement element = gson.toJsonTree(date);

        assertEquals(date.getTime(), element.getAsLong());
    }

    @Test
    public void testUnixTimestampMillis_deserialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeMillis)
            .createGson();

        long time = System.currentTimeMillis();
        JsonElement element = new JsonPrimitive(time);
        Date parsedDate = gson.fromJson(element, Date.class);

        assertEquals(time, parsedDate.getTime());
    }

    @Test
    public void testRFC3999_serialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3999)
            .createGson();

        final Date date = new Date(1360204148123L);
        JsonElement element = gson.toJsonTree(date);

        assertEquals("2013-02-06T21:29:08.123-05:00", element.getAsString());
    }

    @Test
    public void testRFC3999_deserialize(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3999)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:29:08.123-05:00");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148123L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }


}
