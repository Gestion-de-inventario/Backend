package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import com.comedor.backend.domain.model.enums.Status;
import lombok.Data;

import java.util.List;

@Data
public class RolResponseDTO {
    int role_id;
    String name;
    Status status;
    private List<String> permissions;
}
