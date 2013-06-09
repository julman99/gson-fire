package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.PostProcessor;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @autor: julio
 */
public class PostProcessorTypeAdapterFactory implements TypeAdapterFactory {

    private static final JsonParser jsonParser = new JsonParser();

    private Class clazz;
    private PostProcessor postProcessor;

    public PostProcessorTypeAdapterFactory(Class clazz, PostProcessor postProcessor) {
        this.clazz = clazz;
        this.postProcessor = postProcessor;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        boolean shouldHandle = false;
        shouldHandle = clazz.isAssignableFrom(type.getRawType());
        if(shouldHandle){
            final TypeAdapter<T> original = gson.getDelegateAdapter(this, type);
            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    JsonElement res = original.toJsonTree(value);
                    postProcessor.postSerialize(res, value);
                    gson.toJson(res, out);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    JsonElement json = jsonParser.parse(in);
                    T res = original.fromJsonTree(json);
                    postProcessor.postDeserialize(res, json);
                    return res;
                }
            };
        } else {
            return null;
        }
    }
}
