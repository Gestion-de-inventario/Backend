package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.CrearUsuarioUseCase;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.application.ports.out.RolRepositoryPort;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioExistenteException;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Rol;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CrearUsuarioService implements CrearUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final UsuarioMapper usuarioMapper;
    private final RolRepositoryPort rolRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    public CrearUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort, UsuarioMapper usuarioMapper, RolRepositoryPort rolRepositoryPort, PersonaRepositoryPort personaRepositoryPort, PasswordEncoder passwordEncoder) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.usuarioMapper = usuarioMapper;

        this.rolRepositoryPort = rolRepositoryPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {

        if (personaRepositoryPort.existsByDni(dto.getDni())) {
            throw new UsuarioExistenteException("DNI ya registrado");
        }

        if (personaRepositoryPort.existsByNameAndLastName(dto.getName(), dto.getLastname())) {
            throw new UsuarioExistenteException("Nombre y apellido ya existe");
        }

        Usuario usuario = usuarioMapper.toDomain(dto);

        Rol rol = rolRepositoryPort.findById(2)
                .orElseThrow(() -> new RuntimeException("Rol no existe"));

        usuario.setRole(rol);

        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        Estado estado = Estado.ACTIVO;
        usuario.setStatus(estado);
        Usuario saved = usuarioRepositoryPort.save(usuario);
        return usuarioMapper.toUsuarioResponseDTO(saved);
    }
}
