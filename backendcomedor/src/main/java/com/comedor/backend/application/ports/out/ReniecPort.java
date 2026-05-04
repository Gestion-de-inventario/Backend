package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.DatosPersonales;

import java.util.Optional;

public interface ReniecPort {
    Optional<DatosPersonales> consultarPorDni(String dni);
}
