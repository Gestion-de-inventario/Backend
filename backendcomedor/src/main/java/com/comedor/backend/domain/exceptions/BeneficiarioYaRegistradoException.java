package com.comedor.backend.domain.exceptions;

public class BeneficiarioYaRegistradoException extends RuntimeException {
    public BeneficiarioYaRegistradoException(String message) {
        super(message);
    }
}
