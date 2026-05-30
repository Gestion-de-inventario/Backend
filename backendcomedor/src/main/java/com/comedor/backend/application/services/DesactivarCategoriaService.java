package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.CategoryMapper;
import com.comedor.backend.application.ports.in.DesactivarCategoriaUseCase;
import com.comedor.backend.application.ports.in.RegistrarModificacionUseCase;
import com.comedor.backend.application.ports.out.CategoryRepositoryPort;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public class DesactivarCategoriaService implements DesactivarCategoriaUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryMapper categoryMapper;
    private final RegistrarModificacionUseCase registrarModificacionUseCase;
    public DesactivarCategoriaService(CategoryRepositoryPort repository, CategoryMapper mapper, RegistrarModificacionUseCase registrarModificacionUseCase) {
        this.categoryRepositoryPort = repository;
        this.categoryMapper = mapper;
        this.registrarModificacionUseCase = registrarModificacionUseCase;
    }


    @Override
    public CategoriaResponseDTO desactivarCategoriaPorId(int id) {
        CategoriaResponseDTO resultado = categoryMapper.toCategoriaResponseDTO(categoryRepositoryPort.deactivateById(id));
        registrarModificacionUseCase.registrar(new ModificationsRequestDTO(
                "Categoria",
                "status",
                "ACTIVO",
                "INACTIVO"
        ));
        return resultado;
    }
}
