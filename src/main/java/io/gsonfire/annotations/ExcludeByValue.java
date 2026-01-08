package io.gsonfire.annotations;

import io.gsonfire.gson.ExclusionByValueStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field for conditional exclusion during serialization.
 * The specified {@link ExclusionByValueStrategy} determines whether the field
 * should be excluded based on its value.
 *
 * Requires enabling via {@link io.gsonfire.GsonFireBuilder#enableExclusionByValue()}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcludeByValue {
    /**
     * The strategy class that determines whether to exclude this field based on its value.
     * @return The exclusion strategy class
     */
    Class<? extends ExclusionByValueStrategy> value();
}
