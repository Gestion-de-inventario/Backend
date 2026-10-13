package com.comedor.backend.domain.exceptions;

public class DuplicateBeneficiaryException extends BusinessException {
    public DuplicateBeneficiaryException(String message) {
      super(message);
    }
}
