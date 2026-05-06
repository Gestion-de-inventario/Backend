package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonaEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UsuarioEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RolEntityMapper;
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
    private final RolEntityMapper rolEntityMapper;

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
        entity.getPersona().setUser(entity);
        return usuarioEntityMapper.toDomain(usuarioJpaRepository.save(entity));
    }

    @Override
    public Usuario update(Usuario usuario) {

        UsuarioEntity entity = usuarioJpaRepository.findById(usuario.getId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));

        entity.setUsername(usuario.getUsername());
        entity.setPassword(usuario.getPassword());
        entity.setStatus(usuario.getStatus());
        entity.setRole(rolEntityMapper.toEntity(usuario.getRol()));


        if (usuario.getPersona() != null) {

            if (entity.getPersona() == null) {
                entity.setPersona(new PersonaEntity());
            }

            entity.getPersona().setName(usuario.getPersona().getName());
            entity.getPersona().setLastName(usuario.getPersona().getLastname());
            entity.getPersona().setDni(usuario.getPersona().getDni());

            entity.getPersona().setUser(entity);
        }

        UsuarioEntity saved = usuarioJpaRepository.save(entity);

        return usuarioEntityMapper.toDomain(saved);
    }

    @Override
    public List<Usuario> getAllUsuariosActivos() {
       return usuarioEntityMapper.toListDomain(usuarioJpaRepository.getActiveUsers());
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioEntityMapper.toListDomain(usuarioJpaRepository.getAllUsers());
    }

    @Override
    public Usuario deactivateById(Integer id) {
        UsuarioEntity user = usuarioJpaRepository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));
        user.setStatus(Estado.INACTIVO);
        return usuarioEntityMapper.toDomain(usuarioJpaRepository.save(user));
    }
}