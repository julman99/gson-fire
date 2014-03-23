package com.github.julman99.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * @autor: julio
 */
public class DateLongTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(value.getTime());
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        long time = in.nextLong();
        return new Date(time);
    }
}
