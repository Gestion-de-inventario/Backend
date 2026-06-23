package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoryResponseDTO;

public interface ActivateCategoryUseCase {
    CategoryResponseDTO activarCategoriaPorId(int id);
}
