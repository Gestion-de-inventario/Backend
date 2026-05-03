package com.comedor.backend.domain.exceptions;

public class RolNoEncontradoException extends RuntimeException {
    public RolNoEncontradoException() {
        super("El rol no existe");
    }
}
