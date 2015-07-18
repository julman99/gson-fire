package io.gsonfire.postprocessors;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.gson.ExclusionByValueStrategy;
import io.gsonfire.util.FieldInspector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ExclusionByValuePostProcessor implements PostProcessor {

    private final FieldInspector fieldInspector;
    private final Map<Field, String> fieldNameCache = new HashMap<Field,String>();

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

    /**
     * Will try to resolve the name of the field in the {@link JsonObject} using different strategies. First it will
     * look into the {@link SerializedName} annotation, if it cannot find it, it will try all naming policies. Once a
     * match has been found, the naming policy will be cached (it is assumed Gson has only one policy). A cache is
     * maintained also for all missing fields to avoid trying to resolve them again.
     * @param field
     * @param json
     * @return
     */
    private String resolveFieldName(Field field, JsonObject json) {

        if(this.fieldNameCache.containsKey(field)) {
            return this.fieldNameCache.get(field);
        }

        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        if(serializedName != null) {
            return serializedName.value();
        } else if (fieldNamingPolicy != null) {
            //Check if the field exists with the cached fieldNamingPolicy
            String fieldName = fieldNamingPolicy.translateName(field);
            if(json.has(fieldName)) {
                this.fieldNameCache.put(field, fieldName);
                return fieldName;
            }
        }

        //There has been no match, lets brute force and try to find the field name
        for(FieldNamingPolicy candidatePolicy: FieldNamingPolicy.values()) {
            String fieldName = candidatePolicy.translateName(field);
            if(json.has(fieldName)) {
                if(fieldNamingPolicy == null) {
                    fieldNamingPolicy = candidatePolicy;
                }
                this.fieldNameCache.put(field, fieldName);
                return fieldName;
            }
        }

        return null;
    }


}
