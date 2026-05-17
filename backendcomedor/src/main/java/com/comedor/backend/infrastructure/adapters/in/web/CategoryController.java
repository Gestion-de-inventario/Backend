package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ActivarCategoriaUseCase;
import com.comedor.backend.application.ports.in.CrearCategoriaUseCase;
import com.comedor.backend.application.ports.in.DesactivarCategoriaUseCase;
import com.comedor.backend.application.ports.in.ListarCategoriasPorEstadoUseCase;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoriaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ListarCategoriasPorEstadoUseCase listarCategoriasPorEstadoUseCase;
    private final CrearCategoriaUseCase crearCategoriaUseCase;
    private final ActivarCategoriaUseCase activarCategoriaUseCase;
    private final DesactivarCategoriaUseCase desactivarCategoriaUseCase;

    @PreAuthorize("hasAnyRole('PRESIDENTA', 'SOCIA')")
    @GetMapping("/list")
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

    @PreAuthorize("hasRole('PRESIDENTA')")
    @PostMapping("/changeStatus/{id}")
    public CategoriaResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Estado estado)
    {

        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (estado) {
            case ACTIVO -> activarCategoriaUseCase.activarCategoriaPorId(id);
            case INACTIVO -> desactivarCategoriaUseCase.desactivarCategoriaPorId(id);
        };
    }

}
