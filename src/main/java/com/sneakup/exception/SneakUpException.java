package com.sneakup.exception;

public class SneakUpException extends Exception {
    public SneakUpException(String message) {
        super(message);
    }

    public SneakUpException(String message, Throwable cause) {
        super(message, cause);
    }
}