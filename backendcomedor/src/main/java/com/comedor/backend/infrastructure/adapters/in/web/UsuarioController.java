package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final ListarUsuariosActivosUseCase listarUsuariosActivosUseCase;
    private final ListarTodosLosUsuariosUseCase listarTodosLosUsuariosUseCase;
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final EditarUsuarioUseCase editarUsuarioUseCase;
    private final DesactivarUsuarioUseCase desactivarUsuarioUseCase;

    @GetMapping("/all")
    public List<UsuarioResponseDTO> listAllUsers() {
        return listarTodosLosUsuariosUseCase.ListarTodoLosUsuarios();
    }

    @GetMapping("/actived")
    public List<UsuarioResponseDTO> listActivatedUsers() {
        return listarUsuariosActivosUseCase.ListarUsuariosActivos();
    }

    @PostMapping("/register")
    public UsuarioResponseDTO createUser(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return crearUsuarioUseCase.crearUsuario(usuarioRequestDTO);
    }

    @PostMapping("/edit/{id}")
    public UsuarioResponseDTO editUser(@PathVariable Integer id,@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return editarUsuarioUseCase.EditarUsuario(id,usuarioRequestDTO);
    }

    @PostMapping("deactivate/{id}")
    public UsuarioResponseDTO deactivateUser(@PathVariable Integer id) {
        return desactivarUsuarioUseCase.desactivarUsuario(id);
    }
}
