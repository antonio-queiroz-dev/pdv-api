package com.pdvapi.common;

public class MovementNotCancellableException extends RuntimeException {
    public MovementNotCancellableException(String reason) {
        super(reason);
    }
}