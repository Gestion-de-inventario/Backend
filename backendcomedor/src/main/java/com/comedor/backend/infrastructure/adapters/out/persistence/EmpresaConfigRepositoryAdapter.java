package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.EmpresaConfigEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.EmpresaConfigJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaConfigRepositoryAdapter implements EmpresaConfigRepositoryPort {
    private final EmpresaConfigJpaRepository jpaRepository;
    private final EmpresaConfigEntityMapper mapper;

    @Override
    public EmpresaConfig obtener() {
        return jpaRepository.findById(1)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Configuración de empresa no encontrada"));
    }

    @Override
    public EmpresaConfig guardar(EmpresaConfig config) {
        config.setId(1);
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(config)));
    }
}
