package io.gsonfire.util.reflection;

/**
 * Created by julio on 10/1/16.
 */
public interface Factory {
    <T> T get(Class<T> clazz);
}
