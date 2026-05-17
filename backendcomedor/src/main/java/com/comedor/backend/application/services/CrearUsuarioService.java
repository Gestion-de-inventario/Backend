package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.CrearUsuarioUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioExistenteException;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CrearUsuarioService implements CrearUsuarioUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserMapper userMapper;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    public CrearUsuarioService(UserRepositoryPort userRepositoryPort, UserMapper userMapper, RoleRepositoryPort roleRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userRepositoryPort = userRepositoryPort;
        this.userMapper = userMapper;

        this.roleRepositoryPort = roleRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        if (personRepositoryPort.existsByDni(dto.getDni())) {
            throw new UsuarioExistenteException("DNI ya registrado");
        }

        if (personRepositoryPort.existsByNameAndLastName(dto.getName(), dto.getLastname())) {
            throw new UsuarioExistenteException("Nombre y apellido ya existe");
        }

        User user = userMapper.toDomain(dto);

        Role role = roleRepositoryPort.findById(2)
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        user.setRole(role);

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        Estado estado = Estado.ACTIVO;
        user.setStatus(estado);
        User saved = userRepositoryPort.save(user);
        return userMapper.toUsuarioResponseDTO(saved);
    }
}
