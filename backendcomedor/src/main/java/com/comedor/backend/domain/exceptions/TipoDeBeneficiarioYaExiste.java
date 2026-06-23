package com.comedor.backend.domain.exceptions;

public class TipoDeBeneficiarioYaExiste extends RuntimeException {
    public TipoDeBeneficiarioYaExiste(String message) {
        super(message);
    }
}
