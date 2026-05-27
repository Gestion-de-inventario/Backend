package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.DesactivarBeneficiarioUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class DesactivarBeneficiarioService implements DesactivarBeneficiarioUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public DesactivarBeneficiarioService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }

    @Override
    public Beneficiary desactivar(int id) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.desactivar(id);
        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Beneficiario", "status", "ACTIVO", "INACTIVO"
        ));
        return beneficiary;
    }
}
