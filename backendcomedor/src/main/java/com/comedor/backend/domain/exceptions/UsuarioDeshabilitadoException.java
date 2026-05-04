package com.comedor.backend.domain.exceptions;

public class UsuarioDeshabilitadoException extends RuntimeException {
    public UsuarioDeshabilitadoException(String message) {
        super(message);
    }
}
