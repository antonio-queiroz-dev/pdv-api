package com.pdvapi.common;

import com.pdvapi.stock.MovementType;

public class InvalidMovementTypeException extends RuntimeException {
    public InvalidMovementTypeException(MovementType type) {
        super("Invalid movement type for this operation: " + type);
    }
}