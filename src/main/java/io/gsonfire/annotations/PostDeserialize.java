package io.gsonfire.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method to be invoked after the object has been deserialized from JSON.
 * The method can optionally accept {@link com.google.gson.JsonElement} and/or
 * {@link com.google.gson.Gson} parameters for access to the original JSON and Gson instance.
 *
 * Requires enabling hooks via {@link io.gsonfire.GsonFireBuilder#enableHooks(Class)}.
 *
 * @author julio
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostDeserialize {
}
