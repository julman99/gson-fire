package com.github.julman99.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * @autor: julio
 */
public class DateUnixtimeMillisTypeAdapter extends TypeAdapter<Date> {

    private final boolean allowNegativeTimestamp;

    public DateUnixtimeMillisTypeAdapter(boolean allowNegativeTimestamp) {
        this.allowNegativeTimestamp = allowNegativeTimestamp;
    }

    @Override
    public final void write(JsonWriter out, Date value) throws IOException {
        long time = value.getTime();
        if(time >=0 || this.allowNegativeTimestamp){
            out.value(toTimestamp(value));
        } else {
            out.nullValue();
        }
    }

    @Override
    public final Date read(JsonReader in) throws IOException {
        long time = in.nextLong();
        if(time >=0 || this.allowNegativeTimestamp) {
            return fromTimestamp(time);
        } else {
            return null;
        }
    }

    protected long toTimestamp(Date date) {
        return date.getTime();
    }

    protected Date fromTimestamp(long timestamp) {
        return new Date(timestamp);
    }
}
