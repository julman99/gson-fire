package com.github.julman99.gsonfire;

import com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface PostProcessor<T> {

    public void postDeserialize(T result, JsonElement src);

    public void postSerialize(JsonElement result, T src);
}
