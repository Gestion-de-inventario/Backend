package com.comedor.backend.domain.exceptions;

public class CategoriaExistenteException extends RuntimeException {
    public CategoriaExistenteException(String message) {
        super(message);
    }
}
