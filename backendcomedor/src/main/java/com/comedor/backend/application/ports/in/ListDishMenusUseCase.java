package com.comedor.backend.application.ports.in;

import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

import java.util.List;

public interface ListDishMenusUseCase {
    List<DishMenuResponseDTO> list();
}
