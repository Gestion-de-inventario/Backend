package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActivateBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class ActivateBeneficiaryService implements ActivateBeneficiaryUseCase {
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;

    public ActivateBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public Beneficiary activar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.activar(id);
        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Beneficiario",beneficiary.getName(), "status", "INACTIVO", "ACTIVO"
        ));
        return beneficiary;
    }
}
