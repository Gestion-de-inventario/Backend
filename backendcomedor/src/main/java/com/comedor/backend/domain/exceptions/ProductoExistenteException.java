package com.comedor.backend.domain.exceptions;

public class ProductoExistenteException extends RuntimeException {
    public ProductoExistenteException(String message) {
        super(message);
    }
}
