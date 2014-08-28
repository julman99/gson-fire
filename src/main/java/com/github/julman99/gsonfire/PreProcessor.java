package com.github.julman99.gsonfire;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface PreProcessor<T> {

    public void preDeserialize(Class<? extends T> clazz, JsonElement src, Gson gson);

}
