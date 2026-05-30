package com.comedor.backend.domain.exceptions;

public class PrecioMenuInvalido extends RuntimeException {
    public PrecioMenuInvalido(String message) {
        super(message);
    }
}
