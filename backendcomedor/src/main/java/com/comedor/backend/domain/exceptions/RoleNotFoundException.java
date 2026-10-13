package com.comedor.backend.domain.exceptions;

public class RoleNotFoundException extends BusinessException {
    public RoleNotFoundException() {
        super("El rol no existe");
    }
}
