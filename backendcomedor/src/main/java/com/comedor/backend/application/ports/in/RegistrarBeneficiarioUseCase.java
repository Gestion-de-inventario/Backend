package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;

public interface RegistrarBeneficiarioUseCase {
    Beneficiary registrarBeneficiario(Beneficiary beneficiary) ;
}
