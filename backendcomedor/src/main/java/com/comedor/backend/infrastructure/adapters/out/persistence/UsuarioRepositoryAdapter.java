package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.UsuarioEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UsuarioJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;
    private final UsuarioEntityMapper usuarioEntityMapper;

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioJpaRepository.findByUsername(username)
                .map(usuarioEntityMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findById(Integer id) {
        return usuarioJpaRepository.findById(id)
                .map(usuarioEntityMapper::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = usuarioEntityMapper.toEntity(usuario);
        return usuarioEntityMapper.toDomain(usuarioJpaRepository.save(entity));
    }

    @Override
    public List<Usuario> getAllUsuarios() {
       return usuarioEntityMapper.toListDomain(usuarioJpaRepository.findAll());
    }
}