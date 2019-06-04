package io.gsonfire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.gsonfire.annotations.ExposeMethodResult.ConflictResolutionStrategy;

/**
 * Methods annotated with {@link ExposeMethodParam} must take exactly one argument. If enabled, their argument will be parsed from the json
 * data and they will be called with that value during post-processing. Any return values will be ignored by Gson.
 * 
 * @autor piegames
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExposeMethodParam {

    /**
	 * @return The name of the field to call the method with
	 */
    String value();

    /**
     * @return Strategy to be used when there is conflict between the name of a field on the Java Object vs the field name
     */
    ConflictResolutionStrategy conflictResolution() default ConflictResolutionStrategy.OVERWRITE;
}
