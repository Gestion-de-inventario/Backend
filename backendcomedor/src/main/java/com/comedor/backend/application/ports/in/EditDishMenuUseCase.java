package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EditDishMenuRequestDTO;

public interface EditDishMenuUseCase {
    DishMenuResponseDTO edit(Integer id, EditDishMenuRequestDTO request);
}
