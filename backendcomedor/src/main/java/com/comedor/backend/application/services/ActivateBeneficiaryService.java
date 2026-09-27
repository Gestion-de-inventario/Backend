package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActivateBeneficiaryUseCase;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;

import java.util.Map;


public class ActivateBeneficiaryService implements ActivateBeneficiaryUseCase {
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;

    public ActivateBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterAuditUseCase registerAuditUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public Beneficiary activar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.activar(id);
        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Beneficiario",
                        id,
                        beneficiary.getName().concat(" "+beneficiary.getLastname()),
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", "estado",
                                "previousValue", "INACTIVO",
                                "newValue", "ACTIVO"
                        )
                )
        );
        return beneficiary;
    }
}
