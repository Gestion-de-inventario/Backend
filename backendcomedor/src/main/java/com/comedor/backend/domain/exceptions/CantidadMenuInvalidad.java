package com.comedor.backend.domain.exceptions;

public class CantidadMenuInvalidad extends RuntimeException {
    public CantidadMenuInvalidad(String message) {
        super(message);
    }
}
