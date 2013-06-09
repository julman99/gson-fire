package com.github.julman99.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.Date;

/**
 * @autor: julio
 */
public class DateIntegerTypeAdapter extends TypeAdapter<Date> {
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if(value != null){
            out.value(value.getTime() / 1000L);
        } else {
            out.nullValue();
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if(in.peek() != JsonToken.NULL){
            int time = in.nextInt();
            return new Date(time * 1000L);
        } else {
            return null;
        }
    }
}
