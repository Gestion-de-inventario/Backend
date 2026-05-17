package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.PersonalDataReniec;

public interface ConsultarDatosPorDniUseCase {
    PersonalDataReniec consultar(String dni);
}
