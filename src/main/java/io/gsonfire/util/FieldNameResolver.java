package io.gsonfire.util;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
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
        return gson.fieldNamingStrategy();
    }

}
