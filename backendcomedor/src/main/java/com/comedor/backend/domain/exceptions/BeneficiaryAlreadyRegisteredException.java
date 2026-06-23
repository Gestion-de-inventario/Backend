package com.comedor.backend.domain.exceptions;

public class BeneficiaryAlreadyRegisteredException extends RuntimeException {
    public BeneficiaryAlreadyRegisteredException(String message) {
        super(message);
    }
}
