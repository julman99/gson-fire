package io.gsonfire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @autor: julio
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExposeMethodResult {

    enum ConflictResolutionStrategy{
        /**
         * Will overwrite the existing field on the {@link com.google.gson.JsonObject} that conflicts with the field
         * name being written
         */
        OVERWRITE,

        /**
         * Will skip writing to the {@link com.google.gson.JsonObject} if it contains a field with the name being used
         * by this method result
         */
        SKIP
    }

    /**
     * @return The name of the field to store the serialized result of the method
     */
    String value();

    /**
     * @return Strategy to be used when there is conflict between the name of a field on the Java Object vs the field name
     * where the result of the method will be serialized
     */
    ConflictResolutionStrategy conflictResolution() default ConflictResolutionStrategy.OVERWRITE;
}
