package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.domain.model.enums.PermissionCode;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepositoryPort {
    List<Permission> findAll();

    Optional<Permission> findByCode(PermissionCode code);

    Set<Permission> findByCodes(Set<PermissionCode> codes);
}
