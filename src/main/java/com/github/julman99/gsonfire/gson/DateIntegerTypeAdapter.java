package com.github.julman99.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * @autor: julio
 */
public class DateIntegerTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(value.getTime() / 1000L);
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        int time = in.nextInt();
        return new Date(time * 1000L);
    }
}
