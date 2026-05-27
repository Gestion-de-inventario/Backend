package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DishMenuResponseDTO {
    private Integer id;

    private String name;

    private String status;

    private List<DishSupplyResponseDTO> supplies;
}
