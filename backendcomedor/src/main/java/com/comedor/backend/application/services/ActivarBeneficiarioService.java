package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActivarBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class ActivarBeneficiarioService implements ActivarBeneficiarioUseCase {
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ActivarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public Beneficiary activar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.activar(id);
        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Beneficiario", "status", "INACTIVO", "ACTIVO"
        ));
        return beneficiary;
    }
}
