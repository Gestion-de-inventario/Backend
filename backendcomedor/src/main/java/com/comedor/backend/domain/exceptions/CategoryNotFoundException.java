package com.comedor.backend.domain.exceptions;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
