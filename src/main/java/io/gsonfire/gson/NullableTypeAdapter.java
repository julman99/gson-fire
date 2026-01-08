package io.gsonfire.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * A wrapper TypeAdapter that handles null values, delegating to the wrapped adapter
 * only for non-null values. Writes null as JSON null and reads JSON null as Java null.
 *
 * @param <T> The type handled by this adapter
 * @author julio
 */
public final class NullableTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<T> nullable;

    public NullableTypeAdapter(TypeAdapter<T> nullable) {
        this.nullable = nullable;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if(value == null){
            out.nullValue();
        } else {
            nullable.write(out, value);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if(in.peek() == JsonToken.NULL){
            in.nextNull();
            return null;
        } else {
            return nullable.read(in);
        }
    }
}
