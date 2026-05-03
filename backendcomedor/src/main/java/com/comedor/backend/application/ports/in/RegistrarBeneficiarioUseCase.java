package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;

public interface RegistrarBeneficiarioUseCase {
    Beneficiario registrarBeneficiario(Beneficiario beneficiario) ;
}
