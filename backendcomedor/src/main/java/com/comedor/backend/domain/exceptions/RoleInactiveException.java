package com.comedor.backend.domain.exceptions;

public class RoleInactiveException extends RuntimeException {
    public RoleInactiveException(String message) {
        super(message);
    }
}
