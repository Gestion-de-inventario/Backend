package com.comedor.backend.domain.exceptions;

public class BeneficiarioNoEncontradoException extends RuntimeException {
    public BeneficiarioNoEncontradoException(String name) {
         super("Beneficiario No Encontrado: "+ name);
    }
}
