package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.CreateBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeAlreadyExistsException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

import java.util.Map;

public class CreateBeneficiaryTypeService implements CreateBeneficiaryTypeUseCase {
    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryTypeMapper mapper;
    private final RegisterAuditUseCase registerAuditUseCase;
    public CreateBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper, RegisterAuditUseCase registerAuditUseCase) {
        this.repository = repository;
        this.mapper = mapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public BeneficiaryTypeResponseDTO createBeneficiaryType(BeneficiaryTypeRequestDTO requestDTO) {

        if(repository.existsByName(requestDTO.getName().toUpperCase()))
        {
            throw new BeneficiaryTypeAlreadyExistsException("Ya existe un tipo de beneficiario con el nombre : " + requestDTO.getName().toUpperCase());
        }

        BeneficiaryType domain = mapper.convertToDomain(requestDTO);
        BeneficiaryType domainSaved = repository.save(domain);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Tipo de beneficiario",
                        domainSaved.getId(),
                        domainSaved.getName(),
                        AuditAction.CREACION,
                        Map.of(
                                "Nombre", domainSaved.getName(),
                                "Descripcion",domainSaved.getDesc(),
                                "Costo de menu",domainSaved.getMenu_cost()
                        )
                )
        );



        return mapper.convertToDTO(domainSaved);
    }
}
