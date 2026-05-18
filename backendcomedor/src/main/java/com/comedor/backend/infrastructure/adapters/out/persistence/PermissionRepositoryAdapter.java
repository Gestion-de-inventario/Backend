package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.PermissionRepositoryPort;
import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.enums.PermissionCode;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.PermissionEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter
        implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;

    private final PermissionEntityMapper permissionEntityMapper;

    @Override
    public List<Permission> findAll() {

        return permissionEntityMapper.toDomainList(
                permissionJpaRepository.findAll()
        );
    }

    @Override
    public Optional<Permission> findByCode(
            PermissionCode code) {

        return permissionJpaRepository.findByCode(code)
                .map(permissionEntityMapper::toDomain);
    }

    @Override
    public Set<Permission> findByCodes(
            Set<PermissionCode> codes) {

        return permissionEntityMapper.toDomainSet(
                permissionJpaRepository.findByCodeIn(codes)
        );
    }
}
