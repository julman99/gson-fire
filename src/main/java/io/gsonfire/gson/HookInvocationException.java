package io.gsonfire.gson;

public class HookInvocationException extends RuntimeException {
    public HookInvocationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
