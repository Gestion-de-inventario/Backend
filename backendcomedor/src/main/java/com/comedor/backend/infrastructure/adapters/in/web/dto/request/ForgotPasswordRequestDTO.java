package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    private String dni;
    private String phone;
}
