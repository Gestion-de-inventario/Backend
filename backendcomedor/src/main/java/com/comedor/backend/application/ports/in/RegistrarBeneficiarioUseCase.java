package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;

public interface RegistrarBeneficiarioUseCase {
    Beneficiary registrarBeneficiario(BeneficiarioRequestDTO beneficiary) ;
}
