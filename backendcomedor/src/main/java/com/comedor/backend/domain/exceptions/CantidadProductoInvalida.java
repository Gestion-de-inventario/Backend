package com.comedor.backend.domain.exceptions;

public class CantidadProductoInvalida extends RuntimeException {
    public CantidadProductoInvalida(String message) {
        super(message);
    }
}
