package io.gsonfire.gson;

import io.gsonfire.postprocessors.methodinvoker.MappedMethod;

/**
 * Strategy interface for determining whether methods annotated with
 * {@link io.gsonfire.annotations.ExposeMethodResult} should be skipped during serialization.
 *
 * @author julio
 */
public interface FireExclusionStrategy {

    /**
     * Determines whether the given mapped method should be excluded from serialization.
     * @param method The mapped method to evaluate
     * @return true if the method should be skipped, false otherwise
     */
    boolean shouldSkipMethod(MappedMethod method);

}
