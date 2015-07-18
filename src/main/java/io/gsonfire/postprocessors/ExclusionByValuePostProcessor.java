package io.gsonfire.postprocessors;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import io.gsonfire.PostProcessor;
import io.gsonfire.annotations.ExcludeByValue;
import io.gsonfire.gson.ExclusionByValueStrategy;
import io.gsonfire.util.FieldInspector;

import java.lang.reflect.Field;
import java.util.List;

public final class ExclusionByValuePostProcessor implements PostProcessor {

    private final FieldInspector fieldInspector;
    private FieldNamingStrategy fieldNamingStrategy;

    public ExclusionByValuePostProcessor(FieldInspector fieldInspector) {
        this.fieldInspector = fieldInspector;
        this.fieldNamingStrategy = null;
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
                    String fieldName = getFieldName(f, gson);
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

    private String getFieldName(Field f, Gson gson) {
        SerializedName serializedName = f.getAnnotation(SerializedName.class);
        if (serializedName == null) {
            FieldNamingStrategy namingStrategy = getFieldNamingStrategy(gson);
            if (namingStrategy != null) {
                return namingStrategy.translateName(f);
            } else {
                return null;
            }
        } else {
            return serializedName.value();
        }
    }

    private FieldNamingStrategy getFieldNamingStrategy(Gson gson) {
        if (this.fieldNamingStrategy == null) {
            try {
                Field factoriesField = gson.getClass().getDeclaredField("factories");
                factoriesField.setAccessible(true);
                List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) factoriesField.get(gson);

                for (TypeAdapterFactory factory : factories) {
                    if (factory instanceof ReflectiveTypeAdapterFactory) {
                        Field fieldNamingPolicyField = factory.getClass().getDeclaredField("fieldNamingPolicy");
                        fieldNamingPolicyField.setAccessible(true);
                        this.fieldNamingStrategy = (FieldNamingStrategy) fieldNamingPolicyField.get(factory);

                        break;
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return this.fieldNamingStrategy;
    }
}
