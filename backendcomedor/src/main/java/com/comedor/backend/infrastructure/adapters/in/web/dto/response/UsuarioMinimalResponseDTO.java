package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

@Data
public class UsuarioMinimalResponseDTO {
    private Integer user_id;
    private String name;
    private String lastname;
    private String dni;
}
