package io.gsonfire;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * A pre processor that can modify JSON elements before deserialization.
 * Register via {@link GsonFireBuilder#registerPreProcessor(Class, PreProcessor)}.
 *
 * @param <T> The type of objects this pre processor handles
 * @author julio
 */
public interface PreProcessor<T> {

    /**
     * Called before a JSON element is deserialized into an object.
     * Can be used to modify or validate the JSON before deserialization.
     * @param clazz The target class for deserialization
     * @param src The JSON element to be deserialized (can be modified)
     * @param gson The Gson instance used for deserialization
     */
    void preDeserialize(Class<? extends T> clazz, JsonElement src, Gson gson);

}
