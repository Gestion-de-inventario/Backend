package com.comedor.backend.domain.exceptions;

public class BeneficiaryTypeInactiveException extends RuntimeException {
    public BeneficiaryTypeInactiveException(String message) {
        super(message);
    }
}
