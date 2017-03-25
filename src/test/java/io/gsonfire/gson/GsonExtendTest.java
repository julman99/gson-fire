package io.gsonfire.gson;

import com.google.gson.Gson;
import io.gsonfire.DateSerializationPolicy;
import io.gsonfire.GsonFireBuilder;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by julio on 3/25/17.
 */
public class GsonExtendTest {

    @Test
    public void test() {
        Gson gsonDefault = new Gson();

        Gson gsonInner = new GsonFireBuilder()
            .dateSerializationPolicy(DateSerializationPolicy.unixTimeSeconds)
            .createGson();


        Gson gsonOuter = new GsonFireBuilder()
            .extendGson(gsonInner)
            .createGson();

        long timestamp = 1490479024000L;
        Date date = new Date(timestamp);

        assertEquals(timestamp / 1000, gsonOuter.toJsonTree(date).getAsLong());
        assertEquals(timestamp / 1000, gsonInner.toJsonTree(date).getAsLong());
        assertEquals("Mar 25, 2017 5:57:04 PM", gsonDefault.toJsonTree(date).getAsString());
    }

}
