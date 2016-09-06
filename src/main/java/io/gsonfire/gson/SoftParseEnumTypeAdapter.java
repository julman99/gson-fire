package io.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by julio on 9/6/16.
 */
public final class SoftParseEnumTypeAdapter<T extends Enum> extends TypeAdapter {

    private final Class<T> clazz;
    private final T defaultValue;

    public SoftParseEnumTypeAdapter(Class<T> clazz, T defaultValue) {
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    @Override
    public void write(JsonWriter jsonWriter, Object o) throws IOException {
        jsonWriter.value(o.toString());
    }

    @Override
    public Object read(JsonReader jsonReader) throws IOException {
        try {
            return Enum.valueOf(clazz, jsonReader.nextString());
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }
}
