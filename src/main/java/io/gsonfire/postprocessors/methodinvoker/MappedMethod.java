package io.gsonfire.postprocessors.methodinvoker;

import io.gsonfire.annotations.ExposeMethodResult;

import java.lang.reflect.Method;

/**
 * Represents a method annotated with {@link ExposeMethodResult}, storing the method reference,
 * the serialized field name, and the conflict resolution strategy.
 *
 * @author julio
 */
public final class MappedMethod {

    private final  Method method;
    private final String serializedName;
    private final ExposeMethodResult.ConflictResolutionStrategy conflictResolutionStrategy;

    /**
     * Returns the underlying Method object.
     * @return The method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Returns the name to use when serializing the method result to JSON.
     * @return The serialized field name
     */
    public String getSerializedName() {
        return serializedName;
    }

    /**
     * Returns the strategy for handling conflicts with existing fields.
     * @return The conflict resolution strategy
     */
    public ExposeMethodResult.ConflictResolutionStrategy getConflictResolutionStrategy() {
        return conflictResolutionStrategy;
    }

    /**
     * Creates a new MappedMethod.
     * @param method The method to map
     * @param serializedName The name to use in the serialized JSON
     * @param conflictResolutionStrategy The strategy for handling field name conflicts
     */
    public MappedMethod(Method method, String serializedName, ExposeMethodResult.ConflictResolutionStrategy conflictResolutionStrategy) {
        this.method = method;
        this.serializedName = serializedName;
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
}
