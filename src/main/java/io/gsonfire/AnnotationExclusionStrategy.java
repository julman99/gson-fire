package io.gsonfire;

import java.lang.annotation.Annotation;
import java.util.Objects;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * Exclude all fields annotated with a specific annotation.
 * 
 * Forked from <a href="https://stackoverflow.com/a/27986860/6094756">https://stackoverflow.com/a/27986860/6094756<a/>.
 * 
 * @param T
 *            the type of the annotation class
 * @author piegames
 */
public class AnnotationExclusionStrategy<T extends Annotation> implements ExclusionStrategy {

	private Class<? extends T> clazz;

	public AnnotationExclusionStrategy(Class<? extends T> clazz) {
		this.clazz = Objects.requireNonNull(clazz);
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(clazz) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
