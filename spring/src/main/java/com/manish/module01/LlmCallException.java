package com.manish.module01;

/** Typed exception for LLM call failures - never let these fail silently. */
public class LlmCallException extends RuntimeException {
    public LlmCallException(String message, Throwable cause) {
        super(message, cause);
    }
}