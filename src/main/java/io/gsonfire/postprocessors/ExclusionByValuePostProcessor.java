package io.gsonfire.postprocessors;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.gson.ExclusionByValueStrategy;
import io.gsonfire.util.FieldInspector;

import java.lang.reflect.Field;

public final class ExclusionByValuePostProcessor implements PostProcessor {

    private final FieldInspector fieldInspector;

    private FieldNamingPolicy fieldNamingPolicy = null;

    public ExclusionByValuePostProcessor(FieldInspector fieldInspector) {
        this.fieldInspector = fieldInspector;
    }

    @Override
    public void postDeserialize(Object result, JsonElement src, Gson gson) {
        //nothing
    }

    @Override
    public void postSerialize(JsonElement result, Object src, Gson gson) {
        if(src == null){
            return;
        }
        for(Field f: fieldInspector.getAnnotatedFields(src.getClass(), ExcludeByValue.class)){
            try {
                ExcludeByValue excludeByValue = f.getAnnotation(ExcludeByValue.class);
                Class<? extends ExclusionByValueStrategy> exclusionByValueStrategyClass = excludeByValue.value();

                ExclusionByValueStrategy strategy = exclusionByValueStrategyClass.newInstance();
                if (strategy.shouldSkipField(f.get(src))) {
                    JsonObject resultJsonObject = result.getAsJsonObject();
                    String fieldName = resolveFieldName(f, resultJsonObject);
                    if(fieldName != null) {
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

    private String resolveFieldName(Field f, JsonObject json) {
        SerializedName serializedName = f.getAnnotation(SerializedName.class);
        if(serializedName != null) {
            return serializedName.value();
        } else if (fieldNamingPolicy != null) {
            //Check if the field exists with the cached fieldNamingPolicy
            String fieldName = fieldNamingPolicy.translateName(f);
            if(json.has(fieldName)) {
                return fieldName;
            }
        }

        //There has been no match, lets brute force and try to find the field name
        for(FieldNamingPolicy candidatePolicy: FieldNamingPolicy.values()) {
            String fieldName = candidatePolicy.translateName(f);
            if(json.has(fieldName)) {
                if(fieldNamingPolicy == null) {
                    fieldNamingPolicy = candidatePolicy;
                }
                return fieldName;
            }
        }

        return null;
    }


}
