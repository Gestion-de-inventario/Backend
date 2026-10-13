package com.comedor.backend.infrastructure.adapters.in.web.exceptions;

import com.comedor.backend.domain.exceptions.BusinessException;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(message);
    }
}