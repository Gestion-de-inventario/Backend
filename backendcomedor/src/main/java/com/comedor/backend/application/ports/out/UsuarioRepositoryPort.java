package com.comedor.backend.application.ports.out;

import com.comedor.backend.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findById(Integer id);

    Usuario save(Usuario usuario);

    Usuario update(Usuario usuario);

    List<Usuario> getAllUsuariosActivos();

    List<Usuario> getAllUsuarios();

    Usuario deactivateById(Integer id);
}