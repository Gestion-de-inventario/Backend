package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.RegistrarBeneficiarioUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;

public class RegistrarBeneficiarioService implements RegistrarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;
    private final BeneficiaryMapper mapper;
    public RegistrarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort, BeneficiaryMapper mapper) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
        this.mapper = mapper;
    }

    @Override
    public Beneficiary registrarBeneficiario(BeneficiarioRequestDTO beneficiary)  {

        if (beneficiaryRepositoryPort.existePorDni(beneficiary.getDni())) {
            throw new IllegalArgumentException("Ya existe un beneficiario registrado con el DNI " + beneficiary.getDni());
        }

        BeneficiaryType type =  beneficiaryTypeRepositoryPort.findById(beneficiary.getBeneficiaryTypeId());
        Beneficiary domain = mapper.convertToDomain(beneficiary);
        domain.setBeneficiaryType(type);
        return beneficiaryRepositoryPort.guardar(domain);
    }

}
