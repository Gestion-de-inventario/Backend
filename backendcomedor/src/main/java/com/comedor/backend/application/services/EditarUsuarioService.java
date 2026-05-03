package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.PersonaMapper;
import com.comedor.backend.application.common.mapper.UsuarioMapper;
import com.comedor.backend.application.ports.in.EditarUsuarioUseCase;
import com.comedor.backend.application.ports.out.PersonaRepositoryPort;
import com.comedor.backend.application.ports.out.UsuarioRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioExistenteException;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.Persona;
import com.comedor.backend.domain.model.Usuario;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

public class EditarUsuarioService implements EditarUsuarioUseCase {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public EditarUsuarioService(UsuarioMapper usuarioMapper, UsuarioRepositoryPort usuarioRepositoryPort, PersonaRepositoryPort personaRepositoryPort, PasswordEncoder passwordEncoder) {
        this.usuarioMapper = usuarioMapper;
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO EditarUsuario(Integer id, UsuarioRequestDTO dto) {

        Usuario user = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        Persona persona = user.getPersona();

        String newName = dto.getName() != null ? dto.getName() : persona.getName();
        String newLastName = dto.getLastname() != null ? dto.getLastname() : persona.getLastname();
        String newDni = dto.getDni() != null ? dto.getDni() : persona.getDni();

        boolean existsFullName = personaRepositoryPort
                .existsByNameAndLastNameAndIdNot(newName, newLastName, persona.getId());

        if (existsFullName) {
            throw new UsuarioExistenteException(
                    "Ya existe un usuario con ese nombre y apellido"
            );
        }

        boolean existsDni = personaRepositoryPort
                .existsByDniAndIdNot(newDni, persona.getId());

        if (existsDni) {
            throw new UsuarioExistenteException(
                    "Ya existe un usuario con ese DNI"
            );
        }

        persona.setName(newName);
        persona.setLastname(newLastName);
        persona.setDni(newDni);

        user.setUsername(newDni);

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setPersona(persona);


        Usuario updated = usuarioRepositoryPort.update(user);

        return usuarioMapper.toUsuarioResponseDTO(updated);
    }
}
