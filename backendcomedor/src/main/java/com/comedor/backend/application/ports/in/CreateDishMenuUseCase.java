package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateDishMenuRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

public interface CreateDishMenuUseCase {
    DishMenuResponseDTO create(CreateDishMenuRequestDTO request);
}
