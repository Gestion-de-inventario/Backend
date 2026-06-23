package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

public interface EditBeneficiaryTypeUseCase {
    BeneficiaryTypeResponseDTO editBeneficiaryType(Integer id,BeneficiaryTypeRequestDTO request);
}
