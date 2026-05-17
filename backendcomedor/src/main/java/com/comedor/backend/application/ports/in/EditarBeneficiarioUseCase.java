package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarBeneficiarioRequestDTO;

public interface EditarBeneficiarioUseCase {
    Beneficiary editar(int id, EditarBeneficiarioRequestDTO editarBeneficiarioRequest);
}
