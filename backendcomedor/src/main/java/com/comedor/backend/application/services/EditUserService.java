package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.UserMapper;
import com.comedor.backend.application.ports.in.EditUserUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.PersonRepositoryPort;
import com.comedor.backend.application.ports.out.RoleRepositoryPort;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.exceptions.RoleInactiveException;
import com.comedor.backend.domain.exceptions.ExistingUserException;
import com.comedor.backend.domain.exceptions.UserNotFoundException;
import com.comedor.backend.domain.model.Person;
import com.comedor.backend.domain.model.Role;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditUserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;

public class EditUserService implements EditUserUseCase {

    private final UserMapper userMapper;
    private final UserRepositoryPort userRepositoryPort;
    private final PersonRepositoryPort personRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;
    private final RoleRepositoryPort roleRepositoryPort;

    public EditUserService(UserMapper userMapper, UserRepositoryPort userRepositoryPort, PersonRepositoryPort personRepositoryPort, RegisterModificationUseCase registerModificationUseCase, RoleRepositoryPort roleRepositoryPort) {
        this.userMapper = userMapper;
        this.userRepositoryPort = userRepositoryPort;
        this.personRepositoryPort = personRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
        this.roleRepositoryPort = roleRepositoryPort;
    }

    @Override
    public UsuarioResponseDTO EditarUsuario(Integer id, EditUserRequestDTO dto) {

        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        Person person = user.getPersona();

        String newName = dto.getName() != null ? dto.getName() : person.getName();
        String newLastName = dto.getLastname() != null ? dto.getLastname() : person.getLastname();
        String newDni = dto.getDni() != null ? dto.getDni() : person.getDni();
        int newRoleId = dto.getRole_id() != null ? dto.getRole_id() : user.getRol().getId();

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

        // Auditoría solo de campos que realmente cambian
        if (!newName.toUpperCase().equals(person.getName().toUpperCase())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Usuario", "name", person.getName(), newName
            ));
        }

        if (!newLastName.toUpperCase().equals(person.getLastname().toUpperCase())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Usuario", "lastname", person.getLastname(), newLastName
            ));
        }

        if (!newDni.equals(person.getDni())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Usuario", "dni", person.getDni(), newDni
            ));
        }

        Role newRole = roleRepositoryPort.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Rol no existe"));
        if(newRole.getStatus().equals(Status.INACTIVO))
        {
            throw new RoleInactiveException("No se puede asignar un rol inactivo");
        }
        if(newRoleId != user.getRol().getId()) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Usuario","role",user.getRol().getName(),newRole.getName()));
        }

        person.setName(newName.toUpperCase());
        person.setLastname(newLastName.toUpperCase());
        person.setDni(newDni);
        user.setRole(newRole);
        user.setUsername(newDni);
        user.setPersona(person);

        User updated = userRepositoryPort.update(user);

        return userMapper.toUsuarioResponseDTO(updated);
    }
}
