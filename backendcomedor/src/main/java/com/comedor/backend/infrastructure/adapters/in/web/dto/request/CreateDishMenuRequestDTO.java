package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateDishMenuRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotEmpty(message = "Debe tener al menos un insumo")
    private List<DishSupplyRequestDTO> supplies;
}
