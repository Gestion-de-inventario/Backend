package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.CrearUsuarioUseCase;
import com.comedor.backend.application.ports.in.ListarUsuariosUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.UsuarioRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.UsuarioResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final CrearUsuarioUseCase crearUsuarioUseCase;

    @GetMapping("/all")
    public List<UsuarioResponseDTO> login() {
        return listarUsuariosUseCase.ListarUsuarios();
    }

    @PostMapping("/register")
    public UsuarioResponseDTO createUser(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return crearUsuarioUseCase.crearUsuario(usuarioRequestDTO);
    }
}
