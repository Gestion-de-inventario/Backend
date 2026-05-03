package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Beneficiario;

import java.util.Optional;

public interface BeneficiarioRepositoryPort {
    Beneficiario guardar(Beneficiario beneficiario);
    boolean existePorDni(String dni);

}
