package com.comedor.backend.application.ports.out;


import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.enums.Estado;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findById(int id);

    Optional<Role> findByName(String name);

    List<Role> findAll();

    List<Role> findByStatus(Estado status);

    Role save(Role role);

    Role update(Role role);

    boolean existsByName(String name);
}
