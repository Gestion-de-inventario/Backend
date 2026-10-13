package com.comedor.backend.domain.exceptions;

public class DniAlreadyRegisteredException extends BusinessException {
    public DniAlreadyRegisteredException(String message) {
        super(message);
    }
}
