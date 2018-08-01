package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.annotations.FieldTypeAdapter;
import io.gsonfire.util.FieldNameResolver;
import io.gsonfire.util.JsonUtils;
import io.gsonfire.util.reflection.Factory;
import io.gsonfire.util.reflection.FieldInspector;

import java.io.IOException;
import java.lang.reflect.Field;

public class FieldTypeAdapterAdapterFactory implements TypeAdapterFactory {

    private final FieldInspector fieldInspector;
    private final Factory factory;

    private FieldNameResolver fieldNameResolver = null;

    public FieldTypeAdapterAdapterFactory(FieldInspector fieldInspector, Factory factory) {
        this.fieldInspector = fieldInspector;
        this.factory = factory;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(this.fieldNameResolver == null) {
            this.fieldNameResolver = new FieldNameResolver(gson);
        }

        boolean needsDecorating = fieldInspector.getAnnotatedMembers(type.getRawType(), FieldTypeAdapter.class).size() > 0;
        if(needsDecorating) {
            TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
            return new FieldTypeAdapterAdapter(originalTypeAdapter, fieldInspector, factory, fieldNameResolver, gson);
        } else {
            return null;
        }
    }

    private static class FieldTypeAdapterAdapter extends TypeAdapter {

        private final TypeAdapter originalAdapter;
        private final FieldInspector fieldInspector;
        private final Factory factory;
        private final FieldNameResolver fieldNameResolver;
        private final Gson gson;

        private FieldTypeAdapterAdapter(TypeAdapter originalAdapter, FieldInspector fieldInspector, Factory factory, FieldNameResolver fieldNameResolver, Gson gson) {
            this.originalAdapter = originalAdapter;
            this.fieldInspector = fieldInspector;
            this.factory = factory;
            this.fieldNameResolver = fieldNameResolver;
            this.gson = gson;
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            JsonElement resultTree = JsonUtils.toJsonTree(originalAdapter, out, value);
            if(resultTree != null) {
                for (Field f : fieldInspector.getAnnotatedMembers(value.getClass(), FieldTypeAdapter.class)) {
                    try {
                        Class<? extends TypeAdapter> adapterClass = f.getAnnotation(io.gsonfire.annotations.FieldTypeAdapter.class).value();
                        TypeAdapter adapter = factory.get(adapterClass);
                        JsonElement serializedValue = JsonUtils.toJsonTree(adapter, out, f.get(value));
                        String fieldName = fieldNameResolver.getFieldName(f);
                        if(fieldName != null) {
                            resultTree.getAsJsonObject().add(fieldNameResolver.getFieldName(f), serializedValue);
                        }
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            gson.toJson(resultTree, out);
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonObject srcTree = new JsonParser().parse(in).getAsJsonObject();
            Object result = originalAdapter.fromJsonTree(srcTree);
            if(result != null) {
                for (Field f : fieldInspector.getAnnotatedMembers(result.getClass(), io.gsonfire.annotations.FieldTypeAdapter.class)) {
                    try {
                        Class<? extends TypeAdapter> adapterClass = f.getAnnotation(io.gsonfire.annotations.FieldTypeAdapter.class).value();
                        TypeAdapter adapter = factory.get(adapterClass);
                        String fieldName = fieldNameResolver.getFieldName(f);
                        if(fieldName != null && srcTree.has(fieldName)) {
                            Object fieldValue = adapter.fromJsonTree(srcTree.get(fieldName));
                            f.set(result, fieldValue);
                        }
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            return result;
        }
    }

    public static class SerializationExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(FieldTypeAdapter.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
