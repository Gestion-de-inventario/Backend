package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.ActivarCategoriaUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class ActivarCategoriaService implements ActivarCategoriaUseCase {
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;

    public ActivarCategoriaService(CategoryRepositoryPort categoryRepositoryPort, CategoryMapper categoryMapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.categoryRepositoryPort = categoryRepositoryPort;
        this.categoryMapper = categoryMapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }


    @Override
    public CategoriaResponseDTO activarCategoriaPorId(int id) {
        CategoriaResponseDTO categoriaResponseDTO = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.activateById(id));

        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Categoria",
                "status",
                "INACTIVO",
                "ACTIVO"
        ));

        return categoriaResponseDTO;
    }
}
