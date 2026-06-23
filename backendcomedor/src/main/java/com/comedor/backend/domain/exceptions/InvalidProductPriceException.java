package com.comedor.backend.domain.exceptions;

public class InvalidProductPriceException extends RuntimeException {
    public InvalidProductPriceException(String message) {
        super(message);
    }
}
