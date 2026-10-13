package com.comedor.backend.domain.exceptions;

public class InvalidProfileUpdateException extends BusinessException {
    public InvalidProfileUpdateException(String message) {
        super(message);
    }
}
