package com.comedor.backend.domain.exceptions;

public class ExistingProductException extends BusinessException {
    public ExistingProductException(String message) {
        super(message);
    }
}
