package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.EditUserProfileUseCase;
import com.comedor.backend.application.ports.in.EditUserUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.ExistingUserException;
import com.comedor.backend.domain.exceptions.InvalidProfileUpdateException;
import com.comedor.backend.domain.exceptions.RoleInactiveException;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditUserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.Objects;

public class EditUserProfileService implements EditUserProfileUseCase {

    private final UserMapper userMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;
    private final RoleRepositoryPort roleRepositoryPort;

    public EditUserProfileService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, RegisterAuditUseCase registerAuditUseCase, RoleRepositoryPort roleRepositoryPort) {
        this.userMapper = userMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public UsuarioResponseDTO EditarPerfil(EditUserRequestDTO dto) {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        User user =
                userRepositoryPort
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new UserNotFoundException(
                                        "Usuario no encontrado: " + username
                                )
                        );

        Person person = user.getPersona();

        String newName = dto.getName() != null ? dto.getName() : person.getName();
        String newLastName = dto.getLastname() != null ? dto.getLastname() : person.getLastname();
        String newDni = dto.getDni() != null ? dto.getDni() : person.getDni();
        String newPhone = dto.getPhone() != null ? dto.getPhone() : user.getPhone();

        boolean existsFullName = personRepositoryPort
                .existsByNameAndLastNameAndIdNot(newName.toUpperCase(), newLastName.toUpperCase(), person.getId());
        if (existsFullName) {
            throw new ExistingUserException("Ya existe un usuario con ese nombre y apellido");
        }

        boolean existsDni = personRepositoryPort
                .existsByDniAndIdNot(newDni, person.getId());
        if (existsDni) {
            throw new ExistingUserException("Ya existe un usuario con ese DNI");
        }

        if (userRepositoryPort.existsByPhoneAndIdNot(dto.getPhone(),person.getId())) {
            throw new ExistingUserException("El número de teléfono " + dto.getPhone()+" ya esta registrado");
        }

        String actualName = person.getName();
        String actualLastName =  person.getLastname();
        String actualDni = person.getDni();
        String actualPhone = user.getPhone();

        person.setName(newName.toUpperCase());
        person.setLastname(newLastName.toUpperCase());
        person.setDni(newDni);
        user.setPhone(newPhone);
        user.setRole(user.getRol());
        user.setUsername(newDni);
        user.setPersona(person);

        User updated = userRepositoryPort.update(user);

        // Auditoría solo de campos que realmente cambian
        if (!newName.toUpperCase().equals(actualName.toUpperCase())) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Usuario",
                            user.getId(),
                            person.getName().concat(" " + person.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "nombre",
                                    "previousValue", actualName,
                                    "newValue", newName
                            )
                    )
            );
        }

        if (!newLastName.toUpperCase().equals(actualLastName.toUpperCase())) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Usuario",
                            user.getId(),
                            person.getName()
                                    .concat(" " + person.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "apellido",
                                    "previousValue", actualLastName,
                                    "newValue", newLastName
                            )
                    )
            );
        }

        if (!newDni.equals(actualDni)) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Usuario",
                            user.getId(),
                            person.getName()
                                    .concat(" " + person.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "dni",
                                    "previousValue", actualDni,
                                    "newValue", newDni
                            )
                    )
            );
        }

        if(!newPhone.equals(actualPhone)) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Usuario",
                            user.getId(),
                            person.getName()
                                    .concat(" " + person.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "teléfono",
                                    "previousValue", actualPhone,
                                    "newValue", newPhone
                            )
                    )
            );
        }

        return userMapper.toUsuarioResponseDTO(updated);
    }
}
