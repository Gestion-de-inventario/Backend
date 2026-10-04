package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

@Data
public class ValidateResetTokenResponseDTO {
    private boolean valid;
    private String message;
}
