package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.annotations.Wrap;

import java.io.IOException;

/**
 * Created by asanchez on 19/02/16.
 */
public class WrapTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
        Wrap wrap = type.getRawType().getAnnotation(Wrap.class);
        if (wrap == null) {
            return originalTypeAdapter;
        } else {
            return new WrapperTypeAdapter(wrap, gson, originalTypeAdapter);
        }
    }

    private class WrapperTypeAdapter<T> extends TypeAdapter<T> {

        private Wrap wrap;
        private Gson gson;
        private TypeAdapter<T> originalTypeAdapter;

        public WrapperTypeAdapter(Wrap wrap, Gson gson, TypeAdapter<T> originalTypeAdapter) {
            this.wrap = wrap;
            this.gson = gson;
            this.originalTypeAdapter = originalTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, T src) throws IOException {
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src);
            } else {
                final String value = wrap.value();
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
