package com.comedor.backend.domain.exceptions;

public class BeneficiaryNotFoundException extends BusinessException {
    public BeneficiaryNotFoundException(String name) {
         super("Beneficiario No Encontrado: "+ name);
    }
}
