package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
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
    @PostMapping("/edit/{id}")
    public UsuarioResponseDTO editUser(@PathVariable Integer id,@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return editarUsuarioUseCase.EditarUsuario(id,usuarioRequestDTO);
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
