package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.ListBeneficiariesByStatusUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryResponseDTO;

import java.util.List;

public class ListBeneficiariesByStatusService implements ListBeneficiariesByStatusUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryMapper beneficiaryMapper;

    public ListBeneficiariesByStatusService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    @Override
    public List<BeneficiaryResponseDTO> listarBeneficiarioPorEstado(Status status) {
        return beneficiaryMapper.convertToListDTO(beneficiaryRepositoryPort.getBeneficiarioByStatus(status));
    }
}
