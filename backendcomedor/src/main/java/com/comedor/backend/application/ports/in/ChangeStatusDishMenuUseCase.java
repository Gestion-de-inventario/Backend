package com.comedor.backend.application.ports.in;

import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.DishMenuResponseDTO;

public interface ChangeStatusDishMenuUseCase {
    DishMenuResponseDTO changeStatus(Integer id, Status status);
}
