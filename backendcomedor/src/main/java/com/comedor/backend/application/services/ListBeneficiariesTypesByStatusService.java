package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.ListBeneficiariesTypesByStatusUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

import java.util.List;

public class ListBeneficiariesTypesByStatusService implements ListBeneficiariesTypesByStatusUseCase {
    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryTypeMapper mapper;

    public ListBeneficiariesTypesByStatusService(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public List<BeneficiaryTypeResponseDTO> listByStatus(Estado status) {
        return mapper.convertToListDTO(repository.findByStatus(status));
    }
}
