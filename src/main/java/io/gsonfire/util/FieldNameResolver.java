package io.gsonfire.util;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class FieldNameResolver {

    private final Map<Gson,FieldNamingStrategy> fieldNamingStrategies = new WeakHashMap<Gson, FieldNamingStrategy>();
    private final Map<FieldNamingStrategy,Map<Field,String>> fieldNamingStrategiesFieldsCache = new WeakHashMap<FieldNamingStrategy,Map<Field,String>>();

    public String getFieldName(final Field field, final Gson gson) {
        FieldNamingStrategy fieldNamingStrategy = getFieldNamingStrategy(gson);
        Map<Field,String> fieldNamingStrategyFieldsCache = getFieldNamingStrategyFieldsCache(fieldNamingStrategy);
        String fieldName = fieldNamingStrategyFieldsCache.get(field);
        if(fieldName == null){
            SerializedName serializedName = field.getAnnotation(SerializedName.class);
            if (serializedName == null) {
                fieldName = getFieldNamingStrategy(gson).translateName(field);
            } else {
                fieldName = serializedName.value();
            }

            if(!fieldNamingStrategyFieldsCache.containsKey(field)){
                fieldNamingStrategyFieldsCache.put(field, fieldName);
            }
        }

        return fieldName;
    }

    private FieldNamingStrategy getFieldNamingStrategy(Gson gson) {
        FieldNamingStrategy fieldNamingStrategy = this.fieldNamingStrategies.get(gson);

        if (fieldNamingStrategy == null) {
            try {
                Field factoriesField = gson.getClass().getDeclaredField("factories");
                factoriesField.setAccessible(true);
                List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) factoriesField.get(gson);

                for (TypeAdapterFactory factory : factories) {
                    if (factory instanceof ReflectiveTypeAdapterFactory) {
                        Field fieldNamingPolicyField = factory.getClass().getDeclaredField("fieldNamingPolicy");
                        fieldNamingPolicyField.setAccessible(true);

                        fieldNamingStrategy = (FieldNamingStrategy) fieldNamingPolicyField.get(factory);
                        this.fieldNamingStrategies.put(gson, fieldNamingStrategy);

                        break;
                    }
                }
                if (fieldNamingStrategy == null) {
                    throw new NullPointerException("fieldNamingStrategy cannot be resolved");
                }
            } catch (Exception e) {
                throw new RuntimeException("Could not get field naming strategy, the version of Gson currently in use might not be supported.", e);
            }
        }

        return fieldNamingStrategy;
    }

    private Map<Field,String> getFieldNamingStrategyFieldsCache(FieldNamingStrategy fieldNamingStrategy) {
        Map<Field,String> fieldsCache = this.fieldNamingStrategiesFieldsCache.get(fieldNamingStrategy);
        if(fieldsCache != null) {
            return fieldsCache;
        }

        synchronized (this.fieldNamingStrategiesFieldsCache) {
            fieldsCache = this.fieldNamingStrategiesFieldsCache.get(fieldNamingStrategy);
            if(fieldsCache == null) {
                fieldsCache = new HashMap<Field, String>();
                this.fieldNamingStrategiesFieldsCache.put(fieldNamingStrategy, fieldsCache);
            }
        }

        return fieldsCache;
    }

}
