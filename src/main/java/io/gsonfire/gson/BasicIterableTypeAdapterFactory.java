package io.gsonfire.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import io.gsonfire.util.BasicIterable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by julio on 6/23/15.
 */
public final class BasicIterableTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public TypeAdapter create(Gson gson, TypeToken type) {
        if(type.getRawType() == BasicIterable.class) {
            if(type.getType() instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type.getType();
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];
                return new BasicIterableTypeAdapter(gson, typeArgument);
            } else {
                return new BasicIterableTypeAdapter(gson, Object.class);
            }
        } else {
            return null;
        }
    }

}
