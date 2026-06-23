package com.comedor.backend.domain.exceptions;

public class ExistingTagException extends RuntimeException {
    public ExistingTagException(String message) {
        super(message);
    }
}
