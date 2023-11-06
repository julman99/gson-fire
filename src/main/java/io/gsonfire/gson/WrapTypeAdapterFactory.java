package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.util.JsonUtils;
import io.gsonfire.util.Mapper;

import java.io.IOException;

/**
 * Created by asanchez on 19/02/16.
 */
public class WrapTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final Class<T> clazz;
    private final Mapper<T, String> mapper;

    public WrapTypeAdapterFactory(Class<T> clazz, Mapper<T, String> mapper) {
        this.clazz = clazz;
        this.mapper = mapper;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(clazz.isAssignableFrom(type.getRawType())){
            final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
            return new NullableTypeAdapter<T>(new WrapperTypeAdapter(mapper, gson, originalTypeAdapter));
        } else {
            return null;
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
                JsonElement unwrappedObj = JsonUtils.toJsonTree(originalTypeAdapter, out, src);
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
