package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.CreateUserUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingUserException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Locale;
import java.util.Map;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final RegisterAuditUseCase registerAuditUseCase;
    public CreateUserService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RoleRepositoryPort roleRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder, RegisterAuditUseCase registerAuditUseCase) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;

        this.roleRepositoryPort = roleRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.registerAuditUseCase = registerAuditUseCase;
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

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Usuario",
                        saved.getId(),
                        saved.getPersona().getName()
                                .concat(" " + saved.getPersona().getLastname()),
                        AuditAction.CREACION,
                        Map.of(
                                "DNI", saved.getPersona().getDni(),
                                "Nombre", saved.getPersona().getName(),
                                "Apellido", saved.getPersona().getLastname(),
                                "Rol", saved.getRol().getName()
                        )
                )
        );

        return userMapper.toUsuarioResponseDTO(saved);
    }
}
