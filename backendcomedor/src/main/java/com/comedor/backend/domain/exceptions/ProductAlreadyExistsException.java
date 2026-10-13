package com.comedor.backend.domain.exceptions;

public class ProductAlreadyExistsException extends BusinessException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
