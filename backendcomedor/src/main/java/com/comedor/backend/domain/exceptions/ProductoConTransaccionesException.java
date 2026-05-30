package com.comedor.backend.domain.exceptions;

public class ProductoConTransaccionesException extends RuntimeException {
    public ProductoConTransaccionesException(String message) {
        super(message);
    }
}
