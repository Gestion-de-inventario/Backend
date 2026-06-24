package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.EmpresaConfig;

public interface EmpresaConfigRepositoryPort {
    EmpresaConfig obtener();
    EmpresaConfig guardar(EmpresaConfig config);
}
