package com.comedor.backend.domain.exceptions;

public class QuantityMenuInvalidException extends RuntimeException {
    public QuantityMenuInvalidException(String message) {
        super(message);
    }
}
