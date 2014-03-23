package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.util.RFC3339DateFormat;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public class DateRFC3339TypeAdapter extends TypeAdapter<Date> {

    private final RFC3339DateFormat dateFormat;

    /**
     * Creates a RFC3339 type adapter that will serialize dates in UTC
     */
    public DateRFC3339TypeAdapter() {
        this.dateFormat = new RFC3339DateFormat();
    }

    /**
     * Creates a RFC3339 type adapter that will serialize dates using the provided timezone
     */
    public DateRFC3339TypeAdapter(TimeZone serializationTimezone) {
        this.dateFormat = new RFC3339DateFormat(serializationTimezone);
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(dateFormat.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL){
            return null;
        } else {
            final String dateStr = in.nextString();
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                throw new IOException("Could not parse date " + dateStr, e);
            }
        }
    }
}
