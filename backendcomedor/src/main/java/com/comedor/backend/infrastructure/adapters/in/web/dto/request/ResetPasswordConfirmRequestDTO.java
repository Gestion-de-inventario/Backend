package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class ResetPasswordConfirmRequestDTO {

    private String token;

    private String newPassword;
}