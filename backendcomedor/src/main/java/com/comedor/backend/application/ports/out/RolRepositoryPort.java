package com.comedor.backend.application.ports.out;


import com.comedor.backend.domain.model.Rol;

import java.util.Optional;

public interface RolRepositoryPort {
    Optional<Rol> findById(int id);
}
