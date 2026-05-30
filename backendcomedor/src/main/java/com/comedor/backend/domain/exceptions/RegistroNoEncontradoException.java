package com.comedor.backend.domain.exceptions;

public class RegistroNoEncontradoException extends RuntimeException {
    public RegistroNoEncontradoException(String message) {
        super(message);
    }
}
