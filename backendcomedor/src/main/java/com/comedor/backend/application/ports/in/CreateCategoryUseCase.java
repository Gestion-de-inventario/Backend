package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CategoryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;


public interface CreateCategoryUseCase {
    CategoryResponseDTO crearCategoria(CategoryRequestDTO categoria);
}
