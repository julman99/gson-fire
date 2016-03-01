package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.util.Mapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by asanchez on 19/02/16.
 */
public class WrapTypeAdapterFactory<T> implements TypeAdapterFactory {

    private Map<Class<T>, Mapper<T, String>> wrappedClasses;

    public WrapTypeAdapterFactory(Map<Class<T>, Mapper<T, String>> wrappedClasses) {
        this.wrappedClasses = wrappedClasses;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
        final Mapper<T, String> mapper = (Mapper<T, String>) wrappedClasses.get(type.getRawType());

        if (mapper == null) {
            return originalTypeAdapter;
        } else {
            return new WrapperTypeAdapter(mapper, gson, originalTypeAdapter);
        }
    }

    private class WrapperTypeAdapter<T> extends TypeAdapter<T> {

        private final Mapper<T, String> mapper;
        private final Gson gson;
        private final TypeAdapter<T> originalTypeAdapter;

        public WrapperTypeAdapter(Mapper<T, String> mapper, Gson gson, TypeAdapter<T> originalTypeAdapter) {
            this.mapper = mapper;
            this.gson = gson;
            this.originalTypeAdapter = originalTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, T src) throws IOException {
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src);
            } else {
                final String value = mapper.map(src);
                JsonElement unwrappedObj = originalTypeAdapter.toJsonTree(src);
                JsonObject wrappedObj = new JsonObject();
                wrappedObj.add(value, unwrappedObj);
                gson.toJson(wrappedObj, out);
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            in.beginObject();
            in.nextName();
            T unwrappedObj = originalTypeAdapter.read(in);
            in.endObject();
            return unwrappedObj;
        }
    }
}
