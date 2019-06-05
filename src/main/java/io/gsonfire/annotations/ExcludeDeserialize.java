package io.gsonfire.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.gson.annotations.Expose;

/**
 * A counter-part to GSON'S {@link Expose} annotation. It is used analogously, but with inverse functionality. Fields marked with this
 * annotation will be excluded, while all others will be serialized.
 * 
 * @see Expose
 * @author piegames
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcludeDeserialize {
}
