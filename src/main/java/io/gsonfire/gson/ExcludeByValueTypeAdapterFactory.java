package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.util.FieldNameResolver;
import io.gsonfire.util.JsonUtils;
import io.gsonfire.util.reflection.Factory;
import io.gsonfire.util.reflection.FieldInspector;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by julio viera and joao santos on 7/25/15.
 */
public final class ExcludeByValueTypeAdapterFactory implements TypeAdapterFactory {

    private final FieldInspector fieldInspector;
    private final Factory factory;

    private FieldNameResolver fieldNameResolver = null;

    public ExcludeByValueTypeAdapterFactory(FieldInspector fieldInspector, Factory factory) {
        this.fieldInspector = fieldInspector;
        this.factory = factory;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        if(this.fieldNameResolver == null) {
            this.fieldNameResolver = new FieldNameResolver(gson);
        }
        boolean needsDecorating = fieldInspector.getAnnotatedMembers(type.getRawType(), ExcludeByValue.class).size() > 0;
        if(needsDecorating) {
            final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
            return new ExcludeByValueTypeAdapter(gson, originalTypeAdapter);
        } else {
            return null;
        }
    }

    private class ExcludeByValueTypeAdapter extends TypeAdapter {

        private final Gson gson;
        private final TypeAdapter originalTypeAdapter;

        public ExcludeByValueTypeAdapter(Gson gson, TypeAdapter originalTypeAdapter) {
            this.gson = gson;
            this.originalTypeAdapter = originalTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, Object src) throws IOException {
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src);
            } else {
                JsonElement postProcessedObject = JsonUtils.toJsonTree(originalTypeAdapter, out, src);

                for(Field f: fieldInspector.getAnnotatedMembers(src.getClass(), ExcludeByValue.class)){
                    try {
                        ExcludeByValue excludeByValue = f.getAnnotation(ExcludeByValue.class);
                        Class<? extends ExclusionByValueStrategy> exclusionByValueStrategyClass = excludeByValue.value();

                        ExclusionByValueStrategy strategy = factory.get(exclusionByValueStrategyClass);
                        Object fieldValue = f.get(src);
                        if (!strategy.shouldSkipField(fieldValue)) {
                            String fieldName = fieldNameResolver.getFieldName(f);
                            if (fieldName != null) {
                                JsonElement serializedFieldValue = gson.toJsonTree(fieldValue);
                                postProcessedObject.getAsJsonObject().add(fieldName, serializedFieldValue);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(postProcessedObject != null) {
                    //postProcessedObject is not null, this means that we in fact excluded some fields, we need to rewrite it
                    gson.toJson(postProcessedObject, out);
                } else {
                    //postProcessedObject was null, this means nothing was excluded, we will just use the original type adapter
                    originalTypeAdapter.write(out, src);
                }
            }
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            return originalTypeAdapter.read(in);
        }
    }

    public static class SerializationExclusionStrategy implements ExclusionStrategy {

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(ExcludeByValue.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

}
