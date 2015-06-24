package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;

/**
 * @autor: julio
 */
public final class CollectionOperationTypeAdapterFactory implements TypeAdapterFactory {

    private Class<? extends Collection> clazz;

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if(Collection.class.isAssignableFrom(type.getRawType())){
            TypeAdapter<T> collectionTypeAdapter = gson.getDelegateAdapter(this, type);
            return (TypeAdapter<T>) new CollectionOperationTypeAdapter((TypeAdapter<Collection>) collectionTypeAdapter);
        } else {
            return null;
        }
    }

}


