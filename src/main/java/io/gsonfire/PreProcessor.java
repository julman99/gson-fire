package io.gsonfire;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @author julio
 */
public interface PreProcessor<T> {

    void preDeserialize(Class<? extends T> clazz, JsonElement src, Gson gson);

}
