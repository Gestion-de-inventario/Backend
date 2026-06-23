package com.comedor.backend.domain.exceptions;

public class DniAlreadyRegisteredException extends RuntimeException {
    public DniAlreadyRegisteredException(String message) {
        super(message);
    }
}
