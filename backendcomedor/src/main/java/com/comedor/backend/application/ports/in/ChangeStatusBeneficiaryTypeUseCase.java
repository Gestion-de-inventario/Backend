package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.ChangeStatus;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

public interface ChangeStatusBeneficiaryTypeUseCase {
    BeneficiaryTypeResponseDTO changeStatus(Integer id, ChangeStatus status);
}
