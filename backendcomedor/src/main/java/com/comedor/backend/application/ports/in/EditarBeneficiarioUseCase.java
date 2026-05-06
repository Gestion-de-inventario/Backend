package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequest;

public interface EditarBeneficiarioUseCase {
    Beneficiario editar(int id, EditarBeneficiarioRequest editarBeneficiarioRequest);
}
