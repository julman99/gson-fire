package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.ClassConfig;

/**
 * @autor: julio
 */
public final class FireTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final ClassConfig<T> classConfig;

    public FireTypeAdapterFactory(ClassConfig<T> classConfig) {
        this.classConfig = classConfig;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(classConfig.getConfiguredClass().isAssignableFrom(type.getRawType())){
            TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
            FireTypeAdapter<T> fireTypeAdapter = new FireTypeAdapter<T>((Class<T>)type.getRawType(), (ClassConfig<T>) classConfig, originalTypeAdapter, gson);
            return fireTypeAdapter;
        } else {
            return null;
        }
    }
}
