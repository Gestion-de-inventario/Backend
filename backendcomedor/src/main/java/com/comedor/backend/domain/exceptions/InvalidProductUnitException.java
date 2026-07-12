package com.comedor.backend.domain.exceptions;

public class InvalidProductUnitException extends RuntimeException {
    public InvalidProductUnitException(String message) {
        super(message);
    }
}
