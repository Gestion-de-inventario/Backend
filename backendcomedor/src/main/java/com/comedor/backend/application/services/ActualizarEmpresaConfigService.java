package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.ActualizarEmpresaConfigUseCase;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EmpresaConfigRequestDTO;

public class ActualizarEmpresaConfigService implements ActualizarEmpresaConfigUseCase {

    private final EmpresaConfigRepositoryPort repository;

    public ActualizarEmpresaConfigService(EmpresaConfigRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public EmpresaConfig actualizar(EmpresaConfigRequestDTO request) {
        EmpresaConfig config = repository.obtener();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            config.setNombre(request.getNombre().toUpperCase());
        }
        if (request.getDescripcion() != null) {
            config.setDescripcion(request.getDescripcion());
        }
        if (request.getLogoBase64() != null) {
            config.setLogoBase64(request.getLogoBase64());
        }

        return repository.guardar(config);
    }
}
