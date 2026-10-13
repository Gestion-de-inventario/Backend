package com.comedor.backend.domain.exceptions;

public class RegisterNotFoundException extends BusinessException {
    public RegisterNotFoundException(String message) {
        super(message);
    }
}
