package com.github.julman99.gsonfire.gson;

import com.github.julman99.gsonfire.TypeSelector;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *
 * @author julio
 */
public class TypeSelectorTypeAdapterFactory implements TypeAdapterFactory {

    private Class clazz;
    private TypeSelector selector;

    public <T> TypeSelectorTypeAdapterFactory(Class<T> clazz, TypeSelector<T> selector) {
        this.clazz = clazz;
        this.selector = selector;
    }

    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {

        if (type.getRawType() == clazz) {
            final TypeAdapter<T> original = gson.getDelegateAdapter(this, type);

            return new TypeAdapter<T>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    original.write(out, value);
                }

                @Override
                public T read(JsonReader in) throws IOException {
                    JsonElement json = new JsonParser().parse(in);
                    Class clazzDeserialize = selector.getClassForElement(json);
                    if(clazzDeserialize == null || clazzDeserialize == clazz){
                        return original.fromJsonTree(json);
                    } else {
                        return (T)gson.fromJson(json, clazzDeserialize);
                    }
                }
            };
        } else {
            return null;
        }


    }
}
