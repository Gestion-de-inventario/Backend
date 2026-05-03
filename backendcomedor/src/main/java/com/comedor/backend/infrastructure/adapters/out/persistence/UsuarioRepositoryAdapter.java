package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioJpaRepository.findByUsername(username)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioJpaRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);
        return usuarioMapper.toDomain(usuarioJpaRepository.save(entity));
    }
}