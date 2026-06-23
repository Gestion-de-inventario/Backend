package com.comedor.backend.domain.exceptions;

public class QuantityProductInvalidException extends RuntimeException {
    public QuantityProductInvalidException(String message) {
        super(message);
    }
}
