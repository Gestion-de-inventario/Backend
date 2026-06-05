package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.infrastructure.adapters.in.web.dto.request.DishSupplyRequestDTO;
import lombok.Data;

import java.util.List;

@Data
public class EditDishMenuRequestDTO {
    private String name;
    private List<DishSupplyRequestDTO> supplies;
}
