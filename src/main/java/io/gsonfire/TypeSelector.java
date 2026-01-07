package io.gsonfire;

import com.google.gson.JsonElement;

/**
 * @author julio
 */
public interface TypeSelector<T> {

    Class<? extends T> getClassForElement(JsonElement readElement);

}
