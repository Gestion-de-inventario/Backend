package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Integer id);

    User save(User user);

    User update(User user);

    List<User> getAllUsuariosActivos();

    List<User> getAllUsuarios();

    User deactivateById(Integer id);
}