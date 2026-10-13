package com.comedor.backend.domain.exceptions;

public class BeneficiaryAlreadyRegisteredException extends BusinessException {
    public BeneficiaryAlreadyRegisteredException(String message) {
        super(message);
    }
}
