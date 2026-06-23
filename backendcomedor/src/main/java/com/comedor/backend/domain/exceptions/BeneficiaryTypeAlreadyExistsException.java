package com.comedor.backend.domain.exceptions;

public class BeneficiaryTypeAlreadyExistsException extends RuntimeException {
    public BeneficiaryTypeAlreadyExistsException(String message) {
        super(message);
    }
}
