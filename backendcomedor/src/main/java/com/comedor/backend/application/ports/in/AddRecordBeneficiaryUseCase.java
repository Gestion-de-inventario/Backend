package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryRecordResponseDTO;

public interface AddRecordBeneficiaryUseCase {
    BeneficiaryRecordResponseDTO
    agregarRegistroBeneficiario(
            int reporteId,
            ControlBeneficiarioRequestDTO dto
    );
}
