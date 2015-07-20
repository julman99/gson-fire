package io.gsonfire.util;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FieldNameResolver {

    private final Map<Field,String> cache = new ConcurrentHashMap<Field,String>();
    private FieldNamingStrategy fieldNamingStrategy;

    public String getFieldName(final Field field, final Gson gson) {
        String fieldName = cache.get(field);
        if(fieldName != null){
            return fieldName;
        }

        synchronized (cache){
            fieldName = cache.get(field);
            if(fieldName == null){
                SerializedName serializedName = field.getAnnotation(SerializedName.class);
                if (serializedName == null) {
                    fieldName = getFieldNamingStrategy(gson).translateName(field);
                } else {
                    fieldName = serializedName.value();
                }

                if(!cache.containsKey(field)){
                    cache.put(field, fieldName);
                }
            }
        }

        return fieldName;
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

        if (this.fieldNamingStrategy == null) {
            throw new RuntimeException("Could not get field naming strategy");
        }

        return this.fieldNamingStrategy;
    }

}
