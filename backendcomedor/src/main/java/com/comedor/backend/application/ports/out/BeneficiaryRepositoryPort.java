package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepositoryPort {
    Beneficiary guardar(Beneficiary beneficiary);
    boolean existePorDni(String dni);

    Optional<Beneficiary> buscarPorDni(String dni);

    Optional<Beneficiary> findById(Integer id);

    List<Beneficiary> getBeneficiarioByStatus(Estado estado);

    Beneficiary activar(Integer id);

    Beneficiary desactivar(Integer id);

    boolean CategoryisItAssignedToBeneficiary(Integer id);
}
