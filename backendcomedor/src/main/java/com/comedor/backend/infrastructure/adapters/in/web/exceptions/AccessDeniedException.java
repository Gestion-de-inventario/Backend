package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}