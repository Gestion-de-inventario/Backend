package com.comedor.backend.domain.exceptions;

public class InvalidProductPriceException extends BusinessException {
    public InvalidProductPriceException(String message) {
        super(message);
    }
}
