package com.comedor.backend.domain.exceptions;

public class ExistingTagException extends BusinessException {
    public ExistingTagException(String message) {
        super(message);
    }
}
