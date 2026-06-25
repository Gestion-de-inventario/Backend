package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EmpresaConfigRequestDTO;

public interface UpdateCompanyConfigUseCase {
    EmpresaConfig actualizar(EmpresaConfigRequestDTO request);
}
