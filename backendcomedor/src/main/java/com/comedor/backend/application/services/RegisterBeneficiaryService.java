package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.in.RegisterBeneficiaryUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryRequestDTO;

import java.util.Map;

public class RegisterBeneficiaryService implements RegisterBeneficiaryUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;
    private final BeneficiaryMapper mapper;
    private final RegisterAuditUseCase registerAuditUseCase;
    public RegisterBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort, BeneficiaryMapper mapper, RegisterAuditUseCase registerAuditUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
        this.mapper = mapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public Beneficiary registrarBeneficiario(BeneficiaryRequestDTO beneficiary)  {

        if (beneficiaryRepositoryPort.existePorDni(beneficiary.getDni())) {
            throw new IllegalArgumentException("Ya existe un beneficiario registrado con el DNI " + beneficiary.getDni());
        }

        BeneficiaryType type =  beneficiaryTypeRepositoryPort.findById(beneficiary.getBeneficiaryTypeId());
        Beneficiary domain = mapper.convertToDomain(beneficiary);
        domain.setBeneficiaryType(type);
        Beneficiary saved = beneficiaryRepositoryPort.guardar(domain);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Beneficiario",
                        saved.getId(),
                        saved.getName()
                                .concat(" " + saved.getLastname()),
                        AuditAction.CREACION,
                        Map.of(
                                "DNI", saved.getDni(),
                                "Nombre", saved.getName(),
                                "Apellido", saved.getLastname(),
                                "Tipo de beneficiario", saved.getBeneficiaryType().getName()
                        )
                )
        );

        return saved;
    }

}
