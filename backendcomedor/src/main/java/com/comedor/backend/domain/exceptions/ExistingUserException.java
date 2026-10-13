package com.comedor.backend.domain.exceptions;

public class ExistingUserException extends BusinessException {
    public ExistingUserException(String message) {
        super(message);
    }
}
