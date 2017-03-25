package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by julio on 3/25/17.
 */
public class GsonExtenderTypeAdapterFactory implements TypeAdapterFactory {

    private final Gson fallbackGson;

    public GsonExtenderTypeAdapterFactory(Gson gson) {
        this.fallbackGson = gson;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                fallbackGson.toJson(fallbackGson.toJsonTree(value), out);
            }

              
            @Override
            public T read(JsonReader in) throws IOException {
                return fallbackGson.fromJson(in, type.getType());
            }
        };
    }
    
}
