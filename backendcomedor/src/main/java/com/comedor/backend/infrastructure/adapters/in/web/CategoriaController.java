package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.CrearCategoriaUseCase;
import com.comedor.backend.application.ports.in.ListarCategoriasPorEstadoUseCase;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categoria")
@RequiredArgsConstructor
public class CategoriaController {
    private final ListarCategoriasPorEstadoUseCase listarCategoriasPorEstadoUseCase;
    private final CrearCategoriaUseCase crearCategoriaUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @GetMapping("/listar")
    public List<CategoriaResponseDTO> listarCategorias(@RequestParam(required = false) Estado estado)
    {
        return listarCategoriasPorEstadoUseCase.listarCategorias(estado);
    }

    @PreAuthorize("hasAnyRole('PRESIDENTA')")
    @PostMapping("/create")
    public CategoriaResponseDTO crearCategoria(@RequestBody CategoriaRequestDTO categoriaRequestDTO)
    {
        return crearCategoriaUseCase.crearCategoria(categoriaRequestDTO);
    }


}
