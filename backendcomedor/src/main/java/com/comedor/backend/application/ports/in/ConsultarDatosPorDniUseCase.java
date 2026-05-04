package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.DatosPersonales;

public interface ConsultarDatosPorDniUseCase {
    DatosPersonales consultar(String dni);
}
