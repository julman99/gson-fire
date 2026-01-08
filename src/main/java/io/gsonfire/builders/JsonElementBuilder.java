package io.gsonfire.builders;

import com.google.gson.JsonElement;

/**
 * Base interface for JSON element builders that provide a fluent API
 * for constructing JSON structures.
 *
 * @param <T> The type of JsonElement this builder creates
 * @author julio
 */
public interface JsonElementBuilder<T extends JsonElement> {

    /**
     * Builds and returns a deep copy of the constructed JSON element.
     * @return A new JsonElement instance
     */
    T build();

}
