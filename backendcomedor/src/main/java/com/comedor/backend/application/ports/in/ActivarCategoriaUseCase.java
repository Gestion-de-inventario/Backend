package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.CategoriaResponseDTO;

public interface ActivarCategoriaUseCase {
    CategoriaResponseDTO activarCategoriaPorId(int id);
}
