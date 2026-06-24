package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ObtenerEmpresaConfigUseCase;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.domain.model.EmpresaConfig;

public class ObtenerEmpresaConfigService implements ObtenerEmpresaConfigUseCase {

    private final EmpresaConfigRepositoryPort repository;

    public ObtenerEmpresaConfigService(EmpresaConfigRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public EmpresaConfig obtener() {
        return repository.obtener();
    }

}
