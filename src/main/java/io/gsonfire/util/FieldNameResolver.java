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
import java.util.concurrent.ConcurrentMap;

public final class FieldNameResolver {

    private final ConcurrentMap<Gson,FieldNamingStrategy> fieldNamingStrategies = new ConcurrentWeakHashMap<Gson, FieldNamingStrategy>();
    private final ConcurrentMap<FieldNamingStrategy,Map<Field,String>> fieldNamingStrategiesFieldsCache = new ConcurrentWeakHashMap<FieldNamingStrategy,Map<Field,String>>();

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
        if (fieldNamingStrategy != null) {
            return fieldNamingStrategy;
        }

        try {
            Field factoriesField = gson.getClass().getDeclaredField("factories");
            factoriesField.setAccessible(true);
            List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) factoriesField.get(gson);

            for (TypeAdapterFactory factory : factories) {
                if (factory instanceof ReflectiveTypeAdapterFactory) {
                    Field fieldNamingPolicyField = factory.getClass().getDeclaredField("fieldNamingPolicy");
                    fieldNamingPolicyField.setAccessible(true);

                    // due to concurrency it is possible that the gson is already there while it wasn't before
                    this.fieldNamingStrategies.putIfAbsent(gson, (FieldNamingStrategy) fieldNamingPolicyField.get(factory));

                    return this.fieldNamingStrategies.get(gson);
                }
            }
            // if we got here, we could not resolve the fieldNamingStrategy, otherwise we would have returned it
            throw new RuntimeException("Could not get field naming strategy, the version of Gson currently in use might not be supported.");
        } catch (Exception e) {
            throw new RuntimeException("Could not get field naming strategy, the version of Gson currently in use might not be supported.", e);
        }
    }

    private Map<Field,String> getFieldNamingStrategyFieldsCache(FieldNamingStrategy fieldNamingStrategy) {
        Map<Field,String> fieldsCache = this.fieldNamingStrategiesFieldsCache.get(fieldNamingStrategy);
        if(fieldsCache != null) {
            return fieldsCache;
        }

        // due to concurrency it is possible that the strategy is already there while it wasn't before
        this.fieldNamingStrategiesFieldsCache.putIfAbsent(fieldNamingStrategy, new HashMap<Field, String>());

        return this.fieldNamingStrategiesFieldsCache.get(fieldNamingStrategy);
    }

}
