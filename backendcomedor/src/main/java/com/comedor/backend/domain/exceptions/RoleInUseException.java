package com.comedor.backend.domain.exceptions;

public class RoleInUseException extends BusinessException {
    public RoleInUseException(String message) {
        super(message);
    }
}
