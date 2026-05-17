package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RoleEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleEntityMapper roleEntityMapper;
    private final RoleJpaRepository roleJpaRepository;
    @Override
    public Optional<Role> findById(int id) {
        return roleJpaRepository.findById(id)
                .map(roleEntityMapper::toDomain);
    }
}
