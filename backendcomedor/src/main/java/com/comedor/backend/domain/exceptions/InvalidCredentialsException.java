package com.comedor.backend.domain.exceptions;

public class InvalidCredentialsException extends BusinessException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}