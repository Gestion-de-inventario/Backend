package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryResponseDTO;

import java.util.List;

public interface ListBeneficiariesByStatusUseCase {
    List<BeneficiaryResponseDTO> listarBeneficiarioPorEstado(Status status);
}
