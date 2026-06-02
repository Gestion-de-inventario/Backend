package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class CambiarPasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
