package com.comedor.backend.domain.exceptions;

public class ExistingCategoryException extends BusinessException {
    public ExistingCategoryException(String message) {
        super(message);
    }
}
