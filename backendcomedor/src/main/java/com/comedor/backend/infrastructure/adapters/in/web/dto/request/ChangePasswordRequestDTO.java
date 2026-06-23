package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class ChangePasswordRequestDTO {
    private String currentPassword;
    private String newPassword;
}
