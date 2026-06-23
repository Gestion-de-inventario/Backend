package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.CreateUserUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingUserException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    public CreateUserService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RoleRepositoryPort roleRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;

        this.roleRepositoryPort = roleRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UsuarioResponseDTO crearUsuario(UserRequestDTO dto) {

        if (personRepositoryPort.existsByDni(dto.getDni())) {
            throw new ExistingUserException("DNI ya registrado");
        }

        if (personRepositoryPort.existsByNameAndLastName(dto.getName().toUpperCase(), dto.getLastname().toUpperCase())) {
            throw new ExistingUserException("Nombre y apellido ya existe");
        }

        User user = userMapper.toDomain(dto);

        Role role = roleRepositoryPort.findById(dto.getRole_id())
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        user.setRole(role);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Status status = Status.ACTIVO;
        user.setStatus(status);
        User saved = userRepositoryPort.save(user);
        return userMapper.toUsuarioResponseDTO(saved);
    }
}
