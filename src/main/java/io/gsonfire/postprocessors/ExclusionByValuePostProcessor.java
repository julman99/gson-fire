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
                    resultJsonObject.remove(getFieldName(f));
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

    private String getFieldName(Field f) {
        SerializedName serializedName = f.getAnnotation(SerializedName.class);
        return serializedName == null ? FieldNamingPolicy.IDENTITY.translateName(f) : serializedName.value();
    }
}
