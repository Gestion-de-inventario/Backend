package com.comedor.backend.domain.exceptions;

public class TagNotFoundException extends BusinessException {
    public TagNotFoundException(String message) {
        super(message);
    }
}
