package io.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * Created by julio on 10/11/15.
 */
abstract class DateUnixtimeTypeAdapter extends TypeAdapter<Date> {

    private final boolean allowNegativeTimestamp;

    public DateUnixtimeTypeAdapter(boolean allowNegativeTimestamp) {
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

    protected abstract long toTimestamp(Date date);

    protected abstract Date fromTimestamp(long timestamp);

}
