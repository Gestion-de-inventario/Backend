package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    private Integer user_id;
    private String role;
    private Estado status;
    private String name;
    private String lastname;
    private String dni;
}
