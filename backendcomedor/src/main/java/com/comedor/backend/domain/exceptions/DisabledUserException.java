package com.comedor.backend.domain.exceptions;

public class DisabledUserException extends BusinessException {
    public DisabledUserException(String message) {
        super(message);
    }
}
