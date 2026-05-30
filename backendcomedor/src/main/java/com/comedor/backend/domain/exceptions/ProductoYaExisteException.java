package com.comedor.backend.domain.exceptions;

public class ProductoYaExisteException extends RuntimeException {
    public ProductoYaExisteException(String message) {
        super(message);
    }
}
