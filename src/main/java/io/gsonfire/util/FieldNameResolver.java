package io.gsonfire.util;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class FieldNameResolver {

    private final FieldNamingStrategy fieldNamingStrategy;
    private final ConcurrentMap<Field,String> fieldNameCache = new ConcurrentHashMap<Field, String>();

    public FieldNameResolver(Gson gson) {
        this.fieldNamingStrategy = getFieldNamingStrategy(gson);
    }

    public String getFieldName(final Field field) {
        String fieldName = fieldNameCache.get(field);
        if(fieldName == null){
            SerializedName serializedName = field.getAnnotation(SerializedName.class);
            if (serializedName == null) {
                fieldName = fieldNamingStrategy.translateName(field);
            } else {
                fieldName = serializedName.value();
            }

            if(!fieldNameCache.containsKey(field)){
                fieldNameCache.put(field, fieldName);
            }
        }
        return fieldName;
    }

    private FieldNamingStrategy getFieldNamingStrategy(Gson gson) {
        try {
            Field factoriesField = gson.getClass().getDeclaredField("factories");
            factoriesField.setAccessible(true);
            List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) factoriesField.get(gson);

            for (TypeAdapterFactory factory : factories) {
                if (factory instanceof ReflectiveTypeAdapterFactory) {
                    Field fieldNamingPolicyField = factory.getClass().getDeclaredField("fieldNamingPolicy");
                    fieldNamingPolicyField.setAccessible(true);

                    return (FieldNamingStrategy) fieldNamingPolicyField.get(factory);
                }
            }
            // if we got here, we could not resolve the fieldNamingStrategy, otherwise we would have returned it
            throw new RuntimeException("Could not get field naming strategy, the version of Gson currently in use might not be supported.");
        } catch (Exception e) {
            throw new RuntimeException("Could not get field naming strategy, the version of Gson currently in use might not be supported.", e);
        }
    }

}
