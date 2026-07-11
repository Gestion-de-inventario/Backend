package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.DeactivateBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class DeactivateBeneficiaryService implements DeactivateBeneficiaryUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;

    public DeactivateBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
    }

    @Override
    public Beneficiary desactivar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.desactivar(id);
        registerModificationUseCase.registrar(new ModificationsRequestDTO(
                "Beneficiario",
                beneficiary.getName().concat(" "+beneficiary.getLastname()), "status", "ACTIVO", "INACTIVO"
        ));
        return beneficiary;
    }
}
