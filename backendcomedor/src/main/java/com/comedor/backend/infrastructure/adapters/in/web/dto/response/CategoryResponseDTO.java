package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

@Data
public class CategoryResponseDTO {
    private int id;
    private String name;
    private Status status;
}
