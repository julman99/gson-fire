package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.ClassConfig;
import io.gsonfire.TypeSelector;

import java.io.IOException;

/**
 * Creates a {@link TypeAdapter} that will run the {@link TypeSelector} and find the {@link TypeAdapter} for the selected
 * type.
 */
public class TypeSelectorTypeAdapterFactory<T> implements TypeAdapterFactory{

    private final ClassConfig<T> classConfig;

    public TypeSelectorTypeAdapterFactory(ClassConfig<T> classConfig) {
        this.classConfig = classConfig;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(classConfig.getConfiguredClass().isAssignableFrom(type.getRawType())){
            TypeSelectorTypeAdapter<T> fireTypeAdapter = new TypeSelectorTypeAdapter<T>(type.getRawType(), classConfig.getTypeSelector(), gson);
            return fireTypeAdapter;
        } else {
            return null;
        }
    }

    private class TypeSelectorTypeAdapter<T> extends TypeAdapter<T> {

        private final Class superClass;
        private final TypeSelector typeSelector;
        private final Gson gson;

        private TypeSelectorTypeAdapter(Class superClass, TypeSelector typeSelector, Gson gson) {
            this.superClass = superClass;
            this.typeSelector = typeSelector;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            TypeAdapter otherTypeAdapter = gson.getDelegateAdapter(TypeSelectorTypeAdapterFactory.this, TypeToken.get(value.getClass()));
            gson.toJson(otherTypeAdapter.toJsonTree(value), out);
        }

        @Override
        public T read(JsonReader in) throws IOException {
            JsonElement json = new JsonParser().parse(in);
            Class deserialize = this.typeSelector.getClassForElement(json);
            if(deserialize == null) {
                deserialize = superClass;
            }
            TypeAdapter otherTypeAdapter = gson.getDelegateAdapter(TypeSelectorTypeAdapterFactory.this, TypeToken.get(deserialize));
            return (T) otherTypeAdapter.fromJsonTree(json);
        }
    }

}
