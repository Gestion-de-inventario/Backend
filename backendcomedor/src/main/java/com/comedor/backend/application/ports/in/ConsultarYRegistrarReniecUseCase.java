package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;

public interface ConsultarYRegistrarReniecUseCase {
    Beneficiary consultarYRegistrar(String dni);
}
