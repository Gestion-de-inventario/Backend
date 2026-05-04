package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

@Data
public class CategoriaResponseDTO {
    private int id;
    private String name;
    private Estado status;
}
