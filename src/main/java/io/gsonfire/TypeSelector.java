package io.gsonfire;

import com.google.gson.JsonElement;

/**
 * A type selector determines which concrete class should be used when deserializing JSON
 * into a polymorphic type hierarchy. Register via {@link GsonFireBuilder#registerTypeSelector(Class, TypeSelector)}.
 *
 * @param <T> The base type for which this selector determines concrete implementations
 * @author julio
 */
public interface TypeSelector<T> {

    /**
     * Determines the concrete class to use for deserializing the given JSON element.
     * @param readElement The JSON element being deserialized
     * @return The concrete class to deserialize into, or null to use the base type
     */
    Class<? extends T> getClassForElement(JsonElement readElement);

}
