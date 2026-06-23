package com.comedor.backend.domain.exceptions;

public class BeneficiaryTypeNotFoundException extends RuntimeException {
    public BeneficiaryTypeNotFoundException(String message) {
        super(message);
    }
}
