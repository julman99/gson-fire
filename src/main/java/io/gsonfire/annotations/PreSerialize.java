package io.gsonfire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be invoked before the object is serialized to JSON.
 * This allows preparation or modification of the object's state before serialization.
 *
 * Requires enabling hooks via {@link io.gsonfire.GsonFireBuilder#enableHooks(Class)}.
 *
 * @author julio
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreSerialize {
}
