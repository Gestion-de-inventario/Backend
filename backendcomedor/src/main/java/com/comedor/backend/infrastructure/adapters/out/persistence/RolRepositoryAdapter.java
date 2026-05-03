package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.RolRepositoryPort;
import com.comedor.backend.domain.model.Rol;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RolEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RolJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor
public class RolRepositoryAdapter implements RolRepositoryPort {

    private final RolEntityMapper rolEntityMapper;
    private final RolJpaRepository rolJpaRepository;
    @Override
    public Optional<Rol> findById(int id) {
        return rolJpaRepository.findById(id)
                .map(rolEntityMapper::toDomain);
    }
}
