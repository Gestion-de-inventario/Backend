package com.comedor.backend.domain.exceptions;

public class InvalidRefreshTokenException extends BusinessException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
