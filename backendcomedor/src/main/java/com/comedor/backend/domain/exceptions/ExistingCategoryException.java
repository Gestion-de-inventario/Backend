package com.comedor.backend.domain.exceptions;

public class ExistingCategoryException extends RuntimeException {
    public ExistingCategoryException(String message) {
        super(message);
    }
}
