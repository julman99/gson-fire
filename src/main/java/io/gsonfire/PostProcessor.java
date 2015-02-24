package io.gsonfire;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface PostProcessor<T> {

    public void postDeserialize(T result, JsonElement src, Gson gson);

    public void postSerialize(JsonElement result, T src, Gson gson);
}
