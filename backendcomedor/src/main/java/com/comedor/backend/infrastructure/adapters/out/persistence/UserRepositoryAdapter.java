package com.comedor.backend.infrastructure.adapters.out.persistence;

import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.PersonEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.entity.UserEntity;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.RoleEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.mapper.UserEntityMapper;
import com.comedor.backend.infrastructure.adapters.out.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;
    private final RoleEntityMapper roleEntityMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findById(id)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userEntityMapper.toEntity(user);
        entity.getPersona().setUser(entity);
        return userEntityMapper.toDomain(userJpaRepository.save(entity));
    }

    @Override
    public User update(User user) {

        UserEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));

        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setStatus(user.getStatus());
        entity.setRole(roleEntityMapper.toEntity(user.getRol()));


        if (user.getPersona() != null) {

            if (entity.getPersona() == null) {
                entity.setPersona(new PersonEntity());
            }

            entity.getPersona().setName(user.getPersona().getName());
            entity.getPersona().setLastName(user.getPersona().getLastname());
            entity.getPersona().setDni(user.getPersona().getDni());

            entity.getPersona().setUser(entity);
        }

        UserEntity saved = userJpaRepository.save(entity);

        return userEntityMapper.toDomain(saved);
    }

    @Override
    public List<User> getAllUsuariosActivos() {
       return userEntityMapper.toListDomain(userJpaRepository.getActiveUsers());
    }

    @Override
    public List<User> getAllUsuarios() {
        return userEntityMapper.toListDomain(userJpaRepository.getAllUsers());
    }

    @Override
    public User deactivateById(Integer id) {
        UserEntity user = userJpaRepository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));
        user.setStatus(Estado.INACTIVO);
        return userEntityMapper.toDomain(userJpaRepository.save(user));
    }

    @Override
    public User activateById(Integer id) {
        UserEntity user = userJpaRepository.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));
        user.setStatus(Estado.ACTIVO);
        return userEntityMapper.toDomain(userJpaRepository.save(user));
    }
}