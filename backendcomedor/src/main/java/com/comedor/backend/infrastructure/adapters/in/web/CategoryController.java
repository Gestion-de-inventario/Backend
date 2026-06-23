package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ActivateCategoryUseCase;
import com.comedor.backend.application.ports.in.CreateCategoryUseCase;
import com.comedor.backend.application.ports.in.DeactivateCategoryUseCase;
import com.comedor.backend.application.ports.in.ListCategoriesByStatusUseCase;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final ListCategoriesByStatusUseCase listCategoriesByStatusUseCase;
    private final CreateCategoryUseCase createCategoryUseCase;
    private final ActivateCategoryUseCase activateCategoryUseCase;
    private final DeactivateCategoryUseCase deactivateCategoryUseCase;

    @PreAuthorize("hasAuthority('CATEGORY_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<CategoryResponseDTO> listarCategorias(@RequestParam(required = false) Status status)
    {
        return listCategoriesByStatusUseCase.listarCategorias(status);
    }

    @PreAuthorize("hasAuthority('CATEGORY_CREATE')")
    @PostMapping("/create")
    public CategoryResponseDTO crearCategoria(@RequestBody CategoryRequestDTO categoryRequestDTO)
    {
        return createCategoryUseCase.crearCategoria(categoryRequestDTO);
    }

    @PreAuthorize("hasAuthority('CATEGORY_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public CategoryResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Status status)
    {

        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (status) {
            case ACTIVO -> activateCategoryUseCase.activarCategoriaPorId(id);
            case INACTIVO -> deactivateCategoryUseCase.desactivarCategoriaPorId(id);
        };
    }

}
