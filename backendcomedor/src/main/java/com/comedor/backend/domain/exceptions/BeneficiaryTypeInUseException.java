package com.comedor.backend.domain.exceptions;

public class BeneficiaryTypeInUseException extends BusinessException {
    public BeneficiaryTypeInUseException(String message) {
        super(message);
    }
}
