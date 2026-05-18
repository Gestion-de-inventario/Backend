package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.domain.model.enums.PermissionCode;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PermissionEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.RoleEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RoleEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PermissionJpaRepository;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleEntityMapper roleEntityMapper;
    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;
    @Override
    public Optional<Role> findById(int id) {
        return roleJpaRepository.findById(id)
                .map(roleEntityMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name.toUpperCase())
                .map(roleEntityMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll()
                .stream()
                .map(roleEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Role> findByStatus(Estado status) {
        return roleJpaRepository.findByStatus(status)
                .stream()
                .map(roleEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Role save(Role role) {

        RoleEntity entity = roleEntityMapper.toEntity(role);

        Set<PermissionCode> permissionCodes =
                roleEntityMapper.toPermissionCodes(role);

        Set<PermissionEntity> permissions =
                permissionJpaRepository.findByCodeIn(permissionCodes);

        entity.setPermissions(permissions);

        RoleEntity saved = roleJpaRepository.save(entity);

        return roleEntityMapper.toDomain(saved);
    }

    @Override
    public Role update(Role role) {
        return save(role);
    }

    @Override
    public boolean existsByName(String name) {
        return roleJpaRepository.existsByName(
                name.toUpperCase()
        );
    }
}
