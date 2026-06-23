package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ChangePasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditUserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ForceChangePasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UserRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final ListActiveUsersUseCase listActiveUsersUseCase;
    private final ListAllUsersUseCase listAllUsersUseCase;
    private final CreateUserUseCase createUserUseCase;
    private final EditUserUseCase editUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ForceChangePasswordUseCase forceChangePasswordUseCase;
    private final UserRepositoryPort userRepositoryPort;

    @PreAuthorize("hasAuthority('USER_LIST_ALL')")
    @GetMapping("/all")
    public List<UsuarioResponseDTO> listAllUsers() {
        return listAllUsersUseCase.ListarTodoLosUsuarios();
    }

    @PreAuthorize("hasAuthority('USER_LIST_ACTIVE')")
    @GetMapping("/actived")
    public List<UsuarioResponseDTO> listActivatedUsers() {
        return listActiveUsersUseCase.ListarUsuariosActivos();
    }
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping("/register")
    public UsuarioResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return createUserUseCase.crearUsuario(userRequestDTO);
    }
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @PutMapping("/edit/{id}")
    public UsuarioResponseDTO editUser(@PathVariable Integer id, @RequestBody EditUserRequestDTO editUserRequestDTO) {
        return editUserUseCase.EditarUsuario(id, editUserRequestDTO);
    }
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @PutMapping("/change-password/{id}")
    public ResponseEntity<java.util.Map<String, String>> changePassword(@PathVariable Integer id, @RequestBody ForceChangePasswordRequestDTO dto) {
        forceChangePasswordUseCase.changeForcePassword(id, dto);
        return ResponseEntity.ok(java.util.Map.of("mensaje", "Contraseña actualizada correctamente"));
    }

    @PutMapping("/me/edit")
    public UsuarioResponseDTO editMyProfile(Authentication authentication, @RequestBody EditUserRequestDTO dto) {
        String username = authentication.getName();

        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return editUserUseCase.EditarUsuario(user.getId(), dto);
    }

    @PutMapping("/me/change-password")
    public ResponseEntity<java.util.Map<String, String>> changeMyPassword(Authentication authentication, @RequestBody ChangePasswordRequestDTO dto) {
        String username = authentication.getName();
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        changePasswordUseCase.cambiarPassword(user.getId(), dto);

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Contraseña actualizada correctamente"));
    }


    @PreAuthorize("hasAuthority('USER_DEACTIVATE')")
    @PostMapping("deactivate/{id}")
    public UsuarioResponseDTO deactivateUser(@PathVariable Integer id) {
        return deactivateUserUseCase.desactivarUsuario(id);
    }

    @PreAuthorize("hasAuthority('USER_ACTIVATE')")
    @PostMapping("activate/{id}")
    public UsuarioResponseDTO activateUser(@PathVariable Integer id) {
        return activateUserUseCase.activateUser(id);
    }
}
