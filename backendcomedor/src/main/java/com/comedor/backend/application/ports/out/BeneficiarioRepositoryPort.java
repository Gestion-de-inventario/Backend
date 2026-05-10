package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;
import java.util.Optional;

public interface BeneficiarioRepositoryPort {
    Beneficiario guardar(Beneficiario beneficiario);
    boolean existePorDni(String dni);

    Optional<Beneficiario> buscarPorDni(String dni);

    Optional<Beneficiario> findById(Integer id);

    List<Beneficiario> getBeneficiarioByStatus(Estado estado);
}
