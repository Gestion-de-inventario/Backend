package com.comedor.backend.domain.exceptions;

public class RoleAlreadyExistsException extends BusinessException {
    public RoleAlreadyExistsException(String message) {
        super(message);
    }
}
