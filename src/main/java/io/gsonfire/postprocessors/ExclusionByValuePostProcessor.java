package io.gsonfire.postprocessors;

import com.google.gson.*;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.gson.ExclusionByValueStrategy;
import io.gsonfire.util.FieldInspector;
import io.gsonfire.util.FieldNameResolver;

import java.lang.reflect.Field;

public final class ExclusionByValuePostProcessor implements PostProcessor {

    private final FieldInspector fieldInspector;
    private final FieldNameResolver fieldNameResolver;

    public ExclusionByValuePostProcessor(FieldInspector fieldInspector, FieldNameResolver fieldNameResolver) {
        this.fieldInspector = fieldInspector;
        this.fieldNameResolver = fieldNameResolver;
    }

    @Override
    public void postDeserialize(Object result, JsonElement src, Gson gson) {
        //nothing
    }

    @Override
    public void postSerialize(JsonElement result, Object src, Gson gson) {
        if(src == null || result.isJsonNull() || !result.isJsonObject()){
            return;
        }
        for(Field f: fieldInspector.getAnnotatedFields(src.getClass(), ExcludeByValue.class)){
            try {
                ExcludeByValue excludeByValue = f.getAnnotation(ExcludeByValue.class);
                Class<? extends ExclusionByValueStrategy> exclusionByValueStrategyClass = excludeByValue.value();

                ExclusionByValueStrategy strategy = exclusionByValueStrategyClass.newInstance();
                if (strategy.shouldSkipField(f.get(src))) {
                    JsonObject resultJsonObject = result.getAsJsonObject();
                    String fieldName = fieldNameResolver.getFieldName(f, gson);
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

}
