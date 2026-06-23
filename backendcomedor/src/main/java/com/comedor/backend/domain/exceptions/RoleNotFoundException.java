package com.comedor.backend.domain.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException() {
        super("El rol no existe");
    }
}
