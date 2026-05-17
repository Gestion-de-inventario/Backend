package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.PersonalDataReniec;

import java.util.Optional;

public interface ReniecPort {
    Optional<PersonalDataReniec> consultarPorDni(String dni);
}
