package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Data;

import java.util.List;

@Data
public class RolResponseDTO {
    String name;
    Estado status;
    private List<String> permissions;

}
