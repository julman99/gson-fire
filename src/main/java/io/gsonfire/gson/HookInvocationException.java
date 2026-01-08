package io.gsonfire.gson;

/**
 * Exception thrown when an error occurs during the invocation of
 * {@link io.gsonfire.annotations.PostDeserialize} or {@link io.gsonfire.annotations.PreSerialize} hooks.
 */
public class HookInvocationException extends RuntimeException {
    /**
     * Creates a new HookInvocationException.
     * @param message The error message
     * @param throwable The underlying cause
     */
    public HookInvocationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
