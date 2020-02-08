package io.gsonfire;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public abstract class StringSerializer<T> extends TypeAdapter<T> {

    public abstract String toString(T t);
    public abstract T fromString(String s);

    @Override
    public final void write(JsonWriter jsonWriter, T t) throws IOException {
        jsonWriter.value(toString(t));
    }

    @Override
    public final T read(JsonReader jsonReader) throws IOException {
        return fromString(jsonReader.nextString());
    }

}
