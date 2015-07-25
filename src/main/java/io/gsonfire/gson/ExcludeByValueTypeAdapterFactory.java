package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.util.FieldInspector;
import io.gsonfire.util.FieldNameResolver;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by julio viera and joao santos on 7/25/15.
 */
public final class ExcludeByValueTypeAdapterFactory implements TypeAdapterFactory {

    private final FieldInspector fieldInspector;

    private FieldNameResolver fieldNameResolver = null;

    public ExcludeByValueTypeAdapterFactory(FieldInspector fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        if(this.fieldNameResolver == null) {
            this.fieldNameResolver = new FieldNameResolver(gson);
        }
        final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
        return new ExcludeByValueTypeAdapter(gson, originalTypeAdapter);
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
            JsonElement res = originalTypeAdapter.toJsonTree(src);
            if(res != null  && res.isJsonObject() && ! res.isJsonNull()) {
                JsonObject resultJsonObject = res.getAsJsonObject();
                for(Field f: fieldInspector.getAnnotatedFields(src.getClass(), ExcludeByValue.class)){
                    try {
                        ExcludeByValue excludeByValue = f.getAnnotation(ExcludeByValue.class);
                        Class<? extends ExclusionByValueStrategy> exclusionByValueStrategyClass = excludeByValue.value();

                        ExclusionByValueStrategy strategy = exclusionByValueStrategyClass.newInstance();
                        if (strategy.shouldSkipField(f.get(src))) {
                            String fieldName = fieldNameResolver.getFieldName(f);
                            if (fieldName != null) {
                                resultJsonObject.remove(fieldName);
                            }
                        } else {
                            // continue
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            gson.toJson(res, out);
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            return originalTypeAdapter.read(in);
        }
    }

}
