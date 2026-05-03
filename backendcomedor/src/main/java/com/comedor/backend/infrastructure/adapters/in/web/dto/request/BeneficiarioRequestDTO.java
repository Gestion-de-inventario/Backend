package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BeneficiarioRequestDTO {

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 caracteres")
    private String dni;

    @NotBlank(message = "Los nombres son obligatorios")
    private String name;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String lastname;
}
