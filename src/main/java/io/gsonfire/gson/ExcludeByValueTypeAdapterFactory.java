package io.gsonfire.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.util.FieldNameResolver;
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
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src);
            } else {
                JsonObject postProcessedObject = null; //if we detect there is something we should exclude, this will be !=null

                for(Field f: fieldInspector.getAnnotatedMembers(src.getClass(), ExcludeByValue.class)){
                    try {
                        ExcludeByValue excludeByValue = f.getAnnotation(ExcludeByValue.class);
                        Class<? extends ExclusionByValueStrategy> exclusionByValueStrategyClass = excludeByValue.value();

                        ExclusionByValueStrategy strategy = factory.get(exclusionByValueStrategyClass);
                        if (strategy.shouldSkipField(f.get(src))) {
                            String fieldName = fieldNameResolver.getFieldName(f);
                            if (fieldName != null) {
                                //Here we know there is a field we should exclude
                                //Now let's check if the JsonObject is in memory, if not we will get it
                                //from the originalTypeAdapter
                                if(postProcessedObject == null) {
                                    JsonElement originalResult = originalTypeAdapter.toJsonTree(src);
                                    if(originalResult == null || originalResult.isJsonNull() || !originalResult.isJsonObject()) {
                                        break;
                                    }
                                    postProcessedObject = originalTypeAdapter.toJsonTree(src).getAsJsonObject();
                                }

                                //Remove the excluded field
                                postProcessedObject.remove(fieldName);
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

}
