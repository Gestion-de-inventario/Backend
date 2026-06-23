package com.comedor.backend.domain.exceptions;

public class DuplicateBeneficiaryException extends RuntimeException {
    public DuplicateBeneficiaryException(String message) {
      super(message);
    }
}
