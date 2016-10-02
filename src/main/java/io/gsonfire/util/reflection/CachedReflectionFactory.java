package io.gsonfire.util.reflection;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by julio on 10/1/16.
 */
public class CachedReflectionFactory implements Factory {

    private final ConcurrentMap<Class, SoftReference<Object>> cache = new ConcurrentHashMap<Class, SoftReference<Object>>();

    @Override
    public <T> T get(Class<T> clazz) {
        SoftReference<T> resultRef = (SoftReference<T>) cache.get(clazz);
        if(resultRef != null) {
            T result = resultRef.get();
            if (result != null) {
                return result;
            }
        }

        //We need to create the object
        try {
            T newObject = clazz.newInstance();
            cache.putIfAbsent(clazz, new SoftReference<Object>(newObject));
            return newObject;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}
