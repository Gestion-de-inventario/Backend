package com.comedor.backend.domain.exceptions;

public class MenuPriceInvalidException extends RuntimeException {
    public MenuPriceInvalidException(String message) {
        super(message);
    }
}
