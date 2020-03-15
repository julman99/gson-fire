package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @autor: julio
 */
public class DateSerializationTest {

    private static final TimeZone NY_TIMEZONE = TimeZone.getTimeZone("America/New_York");
    private static final TimeZone CCS_TIMEZONE = TimeZone.getTimeZone("America/Caracas");

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
    public void testUnixTimestampSeconds_serialize_negative(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();

        final Date date = new Date(-1);
        JsonElement element = gson.toJsonTree(date);

        assertEquals(date.getTime() / 1000L, element.getAsLong());
    }

    @Test
    public void testUnixTimestampSeconds_serialize_no_negative(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimePositiveSeconds)
            .createGson();

        final Date date = new Date(-1);
        JsonElement element = gson.toJsonTree(date);

        assertTrue(element.isJsonNull());
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
    public void testUnixTimestampMillis_serialize_negative(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeMillis)
            .createGson();

        final Date date = new Date(-1);
        JsonElement element = gson.toJsonTree(date);

        assertEquals(date.getTime(), element.getAsLong());
    }

    @Test
    public void testUnixTimestampMillis_serialize_no_negative(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimePositiveMillis)
            .createGson();

        final Date date = new Date(-1);
        JsonElement element = gson.toJsonTree(date);

        assertTrue(element.isJsonNull());
    }

    @Test
    public void testRFC3339_serialize_NY(){
        TimeZone.setDefault(NY_TIMEZONE);
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(NY_TIMEZONE)
            .createGson();

        final Date date = new Date(1360204148123L);
        JsonElement element = gson.toJsonTree(date);

        assertEquals("2013-02-06T21:29:08.123-05:00", element.getAsString());
    }

    @Test
    public void testRFC3339_deserialize_NY(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(NY_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:29:08.123-05:00");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148123L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }

    @Test
    public void testRFC3339_serialize_CCS(){
        TimeZone.setDefault(NY_TIMEZONE);
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        final Date date = new Date(1360204148123L);
        JsonElement element = gson.toJsonTree(date);

        assertEquals("2013-02-06T21:59:08.123-04:30", element.getAsString());
    }

    @Test
    public void testRFC3339_deserialize_CSS(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:59:08.123-04:30");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148123L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }

    @Test
    public void testRFC3339_deserialize_CSS_dot1(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:59:08.1-04:30");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148100L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }

    @Test
    public void testRFC3339_deserialize_CSS_dot10(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:59:08.10-04:30");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148100L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }

    @Test
    public void testRFC3339_deserialize_CSS_round_under_5(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:59:08.0001-04:30");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148000L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }

    @Test
    public void testRFC3339_deserialize_CSS_round_over_5(){
        Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        JsonElement element = new JsonPrimitive("2013-02-06T21:59:08.0005-04:30");
        Date parsedDate = gson.fromJson(element, Date.class);

        final Date expected = new Date(1360204148001L);
        assertEquals(expected.getTime(), parsedDate.getTime());
    }


    @Test
    public void test_nullDeserialize(){
        for(DateSerializationPolicy policy: DateSerializationPolicy.values()){
            Gson gson = new GsonFireBuilder()
                .dateSerializationPolicy(policy)
                .createGson();

            Date date = gson.fromJson("null", Date.class);
            assertNull(date);
        }
    }

    @Test
    public void test_nullSerialize(){
        for(DateSerializationPolicy policy: DateSerializationPolicy.values()){
            Gson gson = new GsonFireBuilder()
                .dateSerializationPolicy(policy)
                .createGson();

            Date date = null;
            JsonElement jsonElement = gson.toJsonTree(date);
            assertTrue(jsonElement.isJsonNull());
        }
    }

    @Test
    public void testRFC3339_raceCondition() throws InterruptedException {
        TimeZone.setDefault(NY_TIMEZONE);
        final Gson gson = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.rfc3339)
            .serializeTimeZone(CCS_TIMEZONE)
            .createGson();

        final AtomicBoolean active = new AtomicBoolean(true);
        final Random random = new Random();
        final AtomicInteger successCount = new AtomicInteger();
        final int threadCount = 50;

        for(int i=0;i<threadCount;i++) {
            new Thread(){
                @Override
                public void run() {
                    while (active.get()) {
                        long timestamp = (long)(random.nextDouble() + Long.MAX_VALUE);
                        Date dateToSerialize = new Date(timestamp);
                        String serialized = gson.toJson(dateToSerialize);
                        Date dateDeserialized = gson.fromJson(serialized, Date.class);
                        assertEquals(timestamp, dateDeserialized.getTime());
                    }
                    successCount.incrementAndGet();
                }
            }.start();
        }

        Thread.sleep(500);
        active.set(false);
        Thread.sleep(100);
        assertEquals(threadCount, successCount.get());
    }

}
