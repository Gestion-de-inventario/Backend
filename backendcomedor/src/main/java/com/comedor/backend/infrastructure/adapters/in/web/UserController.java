package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.application.ports.out.UserRepositoryPort;
import com.comedor.backend.domain.model.User;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CambiarPasswordRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditarUsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
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
    private final ListarUsuariosActivosUseCase listarUsuariosActivosUseCase;
    private final ListarTodosLosUsuariosUseCase listarTodosLosUsuariosUseCase;
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final EditarUsuarioUseCase editarUsuarioUseCase;
    private final DesactivarUsuarioUseCase desactivarUsuarioUseCase;
    private final ActivarUsuarioUseCase activarUsuarioUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final UserRepositoryPort userRepositoryPort;

    @PreAuthorize("hasAuthority('USER_LIST_ALL')")
    @GetMapping("/all")
    public List<UsuarioResponseDTO> listAllUsers() {
        return listarTodosLosUsuariosUseCase.ListarTodoLosUsuarios();
    }

    @PreAuthorize("hasAuthority('USER_LIST_ACTIVE')")
    @GetMapping("/actived")
    public List<UsuarioResponseDTO> listActivatedUsers() {
        return listarUsuariosActivosUseCase.ListarUsuariosActivos();
    }
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping("/register")
    public UsuarioResponseDTO createUser(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return crearUsuarioUseCase.crearUsuario(usuarioRequestDTO);
    }
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @PutMapping("/edit/{id}")
    public UsuarioResponseDTO editUser(@PathVariable Integer id, @RequestBody EditarUsuarioRequestDTO editarUsuarioRequestDTO) {
        return editarUsuarioUseCase.EditarUsuario(id,editarUsuarioRequestDTO);
    }
    @PreAuthorize("hasAuthority('USER_EDIT')")
    @PutMapping("/change-password/{id}")
    public ResponseEntity<Void> changePassword(@PathVariable Integer id, @RequestBody CambiarPasswordRequestDTO dto) {
        cambiarPasswordUseCase.cambiarPassword(id, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/edit")
    public UsuarioResponseDTO editMyProfile(Authentication authentication, @RequestBody EditarUsuarioRequestDTO dto) {
        String username = authentication.getName();

        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return editarUsuarioUseCase.EditarUsuario(user.getId(), dto);
    }

    @PutMapping("/me/change-password")
    public ResponseEntity<java.util.Map<String, String>> changeMyPassword(Authentication authentication, @RequestBody CambiarPasswordRequestDTO dto) {
        String username = authentication.getName();
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        cambiarPasswordUseCase.cambiarPassword(user.getId(), dto);

        return ResponseEntity.ok(java.util.Map.of("mensaje", "Contraseña actualizada correctamente"));
    }


    @PreAuthorize("hasAuthority('USER_DEACTIVATE')")
    @PostMapping("deactivate/{id}")
    public UsuarioResponseDTO deactivateUser(@PathVariable Integer id) {
        return desactivarUsuarioUseCase.desactivarUsuario(id);
    }

    @PreAuthorize("hasAuthority('USER_ACTIVATE')")
    @PostMapping("activate/{id}")
    public UsuarioResponseDTO activateUser(@PathVariable Integer id) {
        return activarUsuarioUseCase.activateUser(id);
    }
}
