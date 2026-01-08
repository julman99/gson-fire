package io.gsonfire.gson;

/**
 * Strategy interface for determining whether a field should be excluded from serialization
 * based on its value. Used with the {@link io.gsonfire.annotations.ExcludeByValue} annotation.
 *
 * @param <T> The type of the field value
 */
public interface ExclusionByValueStrategy<T> {

    /**
     * Determines whether a field with the given value should be excluded from serialization.
     * @param fieldValue The current value of the field
     * @return true if the field should be excluded, false otherwise
     */
    boolean shouldSkipField(T fieldValue);

}
