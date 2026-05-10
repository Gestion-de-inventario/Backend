package com.comedor.backend.domain.exceptions;

public class PrecioProductoInvalido extends RuntimeException {
    public PrecioProductoInvalido(String message) {
        super(message);
    }
}
