package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.DeactivateBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;

import java.util.Map;

public class DeactivateBeneficiaryService implements DeactivateBeneficiaryUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;

    public DeactivateBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterAuditUseCase registerAuditUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public Beneficiary desactivar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.desactivar(id);
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Beneficiario",
                        id,
                        beneficiary.getName().concat(" "+beneficiary.getLastname()),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "ACTIVO",
                                "newValue", "INACTIVO"
                        )
                )
        );
        return beneficiary;
    }
}
