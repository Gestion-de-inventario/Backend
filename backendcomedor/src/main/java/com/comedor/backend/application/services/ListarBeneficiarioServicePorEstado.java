package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiarioMapper;
import com.comedor.backend.application.ports.in.ListarBeneficiariosPorEstadoUseCase;
import com.comedor.backend.application.ports.out.BeneficiarioRepositoryPort;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiarioResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public class ListarBeneficiarioServicePorEstado implements ListarBeneficiariosPorEstadoUseCase {

    private final BeneficiarioRepositoryPort beneficiarioRepositoryPort;
    private final BeneficiarioMapper beneficiarioMapper;

    public ListarBeneficiarioServicePorEstado(BeneficiarioRepositoryPort beneficiarioRepositoryPort, BeneficiarioMapper beneficiarioMapper) {
        this.beneficiarioRepositoryPort = beneficiarioRepositoryPort;
        this.beneficiarioMapper = beneficiarioMapper;
    }

    @Override
    public List<BeneficiarioResponseDTO> listarBeneficiarioPorEstado(Estado estado) {
        return beneficiarioMapper.convertToListDTO(beneficiarioRepositoryPort.getBeneficiarioByStatus(estado));
    }
}
