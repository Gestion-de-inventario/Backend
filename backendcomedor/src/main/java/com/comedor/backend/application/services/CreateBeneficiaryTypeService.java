package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.CreateBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.TipoDeBeneficiarioYaExiste;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

public class CreateBeneficiaryTypeService implements CreateBeneficiaryTypeUseCase {
    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryTypeMapper mapper;

    public CreateBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public BeneficiaryTypeResponseDTO createBeneficiaryType(BeneficiaryTypeRequestDTO requestDTO) {

        if(repository.existsByName(requestDTO.getName().toUpperCase()))
        {
            throw new TipoDeBeneficiarioYaExiste("Ya existe un tipo de beneficiario con el nombre : " + requestDTO.getName().toUpperCase());
        }

        BeneficiaryType domain = mapper.convertToDomain(requestDTO);
        BeneficiaryType domainSaved = repository.save(domain);
        return mapper.convertToDTO(domainSaved);
    }
}
