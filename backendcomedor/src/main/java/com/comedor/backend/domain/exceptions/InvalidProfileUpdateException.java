package com.comedor.backend.domain.exceptions;

public class InvalidProfileUpdateException extends RuntimeException {
    public InvalidProfileUpdateException(String message) {
        super(message);
    }
}
