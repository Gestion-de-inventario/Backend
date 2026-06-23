package com.comedor.backend.domain.exceptions;

public class RegisterNotFoundException extends RuntimeException {
    public RegisterNotFoundException(String message) {
        super(message);
    }
}
