package io.gsonfire.gson;

import io.gsonfire.util.RFC3339DateFormat;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public final class DateRFC3339TypeAdapter extends TypeAdapter<Date> {

    private final RFC3339DateFormat dateFormat;

    /**
     * Creates a RFC3339 type adapter that will serialize dates in UTC
     */
    public DateRFC3339TypeAdapter(boolean serializeTime) {
        this.dateFormat = new RFC3339DateFormat(serializeTime);
    }

    /**
     * Creates a RFC3339 type adapter that will serialize dates using the provided timezone
     */
    public DateRFC3339TypeAdapter(TimeZone serializationTimezone, boolean serializeTime) {
        this.dateFormat = new RFC3339DateFormat(serializationTimezone, serializeTime);
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(dateFormat.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        final String dateStr = in.nextString();
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new IOException("Could not parse date " + dateStr, e);
        }
    }
}
