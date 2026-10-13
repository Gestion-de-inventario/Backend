package com.comedor.backend.domain.exceptions;

public class PermissionNotFoundException extends BusinessException {
    public PermissionNotFoundException(String message) {
        super(message);
    }
}
