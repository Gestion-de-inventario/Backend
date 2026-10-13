package com.comedor.backend.domain.exceptions;

public class RefreshTokenExpiredException extends BusinessException {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }
}
