package com.flowly.exception;

public class InvalidSubdomainException extends RuntimeException {
    public InvalidSubdomainException(String message) {
        super(message);
    }
}
