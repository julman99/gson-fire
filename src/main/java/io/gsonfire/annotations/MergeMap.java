package io.gsonfire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Causes the Map to be merged with the object that contains it
 *
 * This class has been deprecated and a {@link io.gsonfire.PostProcessor} should be used instead
 * @autor: julio
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Deprecated
public @interface MergeMap {
}
