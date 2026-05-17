package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryMapper;
import com.comedor.backend.application.ports.in.ListarBeneficiariosPorEstadoUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;

import java.util.List;

public class ListarBeneficiarioServicePorEstado implements ListarBeneficiariosPorEstadoUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryMapper beneficiaryMapper;

    public ListarBeneficiarioServicePorEstado(BeneficiaryRepositoryPort beneficiaryRepositoryPort, BeneficiaryMapper beneficiaryMapper) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.beneficiaryMapper = beneficiaryMapper;
    }

    @Override
    public List<BeneficiarioResponseDTO> listarBeneficiarioPorEstado(Estado estado) {
        return beneficiaryMapper.convertToListDTO(beneficiaryRepositoryPort.getBeneficiarioByStatus(estado));
    }
}
