package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;

public interface ActivateBeneficiaryUseCase {
    Beneficiary activar(int id);
}
