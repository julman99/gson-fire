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
        return new WrapperTypeAdapter(gson, originalTypeAdapter);
    }

    private class WrapperTypeAdapter extends TypeAdapter {

        private Gson gson;
        private TypeAdapter originalTypeAdapter;

        public WrapperTypeAdapter(Gson gson, TypeAdapter originalTypeAdapter) {
            this.gson = gson;
            this.originalTypeAdapter = originalTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, Object src) throws IOException {
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src);
            } else {
                Wrap wrapper = src.getClass().getAnnotation(Wrap.class);
                if (wrapper == null) {
                    // wrapper annotation not present, delegate it to the original type adapter
                    originalTypeAdapter.write(out, src);
                } else {
                    final String value = wrapper.value();
                    JsonElement unwrappedObj = originalTypeAdapter.toJsonTree(src);
                    JsonObject wrappedObj = new JsonObject();
                    wrappedObj.add(value, unwrappedObj);
                    gson.toJson(wrappedObj, out);
                }
            }
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            return originalTypeAdapter.read(in);
        }
    }
}
