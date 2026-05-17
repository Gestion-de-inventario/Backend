package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.EditarUsuarioUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.UsuarioExistenteException;
import com.comedor.backend.domain.exceptions.UsuarioNoEncontradoException;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

public class EditarUsuarioService implements EditarUsuarioUseCase {

    private final UserMapper userMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    public EditarUsuarioService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDTO EditarUsuario(Integer id, UsuarioRequestDTO dto) {

        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        Person person = user.getPersona();

        String newName = dto.getName() != null ? dto.getName() : person.getName();
        String newLastName = dto.getLastname() != null ? dto.getLastname() : person.getLastname();
        String newDni = dto.getDni() != null ? dto.getDni() : person.getDni();

        boolean existsFullName = personRepositoryPort
                .existsByNameAndLastNameAndIdNot(newName, newLastName, person.getId());

        if (existsFullName) {
            throw new UsuarioExistenteException(
                    "Ya existe un usuario con ese nombre y apellido"
            );
        }

        boolean existsDni = personRepositoryPort
                .existsByDniAndIdNot(newDni, person.getId());

        if (existsDni) {
            throw new UsuarioExistenteException(
                    "Ya existe un usuario con ese DNI"
            );
        }

        person.setName(newName);
        person.setLastname(newLastName);
        person.setDni(newDni);

        user.setUsername(newDni);

        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setPersona(person);


        User updated = userRepositoryPort.update(user);

        return userMapper.toUsuarioResponseDTO(updated);
    }
}
