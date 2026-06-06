package com.comedor.backend.domain.exceptions;

public class BeneficiaryTypeInUseException extends RuntimeException {
    public BeneficiaryTypeInUseException(String message) {
        super(message);
    }
}
