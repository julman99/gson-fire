package io.gsonfire;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

/**
 * A post processor that can modify objects after deserialization or JSON elements after serialization.
 * Register via {@link GsonFireBuilder#registerPostProcessor(Class, PostProcessor)}.
 *
 * @param <T> The type of objects this post processor handles
 * @author julio
 */
public interface PostProcessor<T> {

    /**
     * Called after an object has been deserialized from JSON.
     * @param result The deserialized object
     * @param src The original JSON element that was deserialized
     * @param gson The Gson instance used for deserialization
     */
    void postDeserialize(T result, JsonElement src, Gson gson);

    /**
     * Called after an object has been serialized to JSON.
     * @param result The JSON element produced by serialization
     * @param src The original object that was serialized
     * @param gson The Gson instance used for serialization
     */
    void postSerialize(JsonElement result, T src, Gson gson);
}
