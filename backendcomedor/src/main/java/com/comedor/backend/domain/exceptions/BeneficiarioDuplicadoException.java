package com.comedor.backend.domain.exceptions;

public class BeneficiarioDuplicadoException extends RuntimeException {
    public BeneficiarioDuplicadoException(String message) {
      super(message);
    }
}
