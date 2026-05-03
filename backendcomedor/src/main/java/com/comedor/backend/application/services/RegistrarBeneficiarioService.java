package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegistrarBeneficiarioUseCase;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.domain.model.Beneficiario;

public class RegistrarBeneficiarioService implements RegistrarBeneficiarioUseCase {

    private final BeneficiarioRepositoryPort beneficiarioRepositoryPort;

    public RegistrarBeneficiarioService(BeneficiarioRepositoryPort beneficiarioRepositoryPort) {
        this.beneficiarioRepositoryPort = beneficiarioRepositoryPort;
    }

    @Override
    public Beneficiario registrarBeneficiario(Beneficiario beneficiario)  {

        if (beneficiarioRepositoryPort.existePorDni(beneficiario.getDni())) {
            throw new IllegalArgumentException("Ya existe un beneficiario registrado con el DNI " + beneficiario.getDni());
        }
        return beneficiarioRepositoryPort.guardar(beneficiario);
    }

}
