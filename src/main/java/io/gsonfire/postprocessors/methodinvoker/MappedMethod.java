package io.gsonfire.postprocessors.methodinvoker;

import io.gsonfire.annotations.ExposeMethodResult;

import java.lang.reflect.Method;

/**
 * Created by julio on 5/25/15.
 */
public final class MappedMethod {

    private final  Method method;
    private final String serializedName;
    private final ExposeMethodResult.ConflictResolutionStrategy conflictResolutionStrategy;

    public Method getMethod() {
        return method;
    }

    public String getSerializedName() {
        return serializedName;
    }

    public ExposeMethodResult.ConflictResolutionStrategy getConflictResolutionStrategy() {
        return conflictResolutionStrategy;
    }

    public MappedMethod(Method method, String serializedName, ExposeMethodResult.ConflictResolutionStrategy conflictResolutionStrategy) {
        this.method = method;
        this.serializedName = serializedName;
        this.conflictResolutionStrategy = conflictResolutionStrategy;
    }
}
