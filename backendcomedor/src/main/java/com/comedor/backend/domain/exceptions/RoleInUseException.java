package com.comedor.backend.domain.exceptions;

public class RoleInUseException extends RuntimeException {
    public RoleInUseException(String message) {
        super(message);
    }
}
