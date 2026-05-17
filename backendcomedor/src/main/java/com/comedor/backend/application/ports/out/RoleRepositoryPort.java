package com.comedor.backend.application.ports.out;


import com.comedor.backend.domain.model.Role;

import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> findById(int id);
}
