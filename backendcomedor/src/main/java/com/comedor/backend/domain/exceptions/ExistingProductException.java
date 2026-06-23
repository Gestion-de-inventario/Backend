package com.comedor.backend.domain.exceptions;

public class ExistingProductException extends RuntimeException {
    public ExistingProductException(String message) {
        super(message);
    }
}
