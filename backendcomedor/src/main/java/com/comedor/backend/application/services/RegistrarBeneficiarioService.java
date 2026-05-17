package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegistrarBeneficiarioUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;

public class RegistrarBeneficiarioService implements RegistrarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;

    public RegistrarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
    }

    @Override
    public Beneficiary registrarBeneficiario(Beneficiary beneficiary)  {

        if (beneficiaryRepositoryPort.existePorDni(beneficiary.getDni())) {
            throw new IllegalArgumentException("Ya existe un beneficiario registrado con el DNI " + beneficiary.getDni());
        }
        return beneficiaryRepositoryPort.guardar(beneficiary);
    }

}
