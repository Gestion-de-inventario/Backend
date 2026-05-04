package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiario;

public interface ConsultarYRegistrarReniecUseCase {
    Beneficiario consultarYRegistrar(String dni);
}
