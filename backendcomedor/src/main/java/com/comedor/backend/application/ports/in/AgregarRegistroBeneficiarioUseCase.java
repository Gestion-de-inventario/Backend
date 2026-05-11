package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ControlBeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RegistroBeneficiarioResponseDTO;

public interface AgregarRegistroBeneficiarioUseCase {
    RegistroBeneficiarioResponseDTO
    agregarRegistroBeneficiario(
            int reporteId,
            ControlBeneficiarioRequestDTO dto
    );
}
