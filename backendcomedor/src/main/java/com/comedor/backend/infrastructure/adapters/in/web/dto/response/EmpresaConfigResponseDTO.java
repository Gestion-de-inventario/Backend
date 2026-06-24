package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

@Data
public class EmpresaConfigResponseDTO {
    private String nombre;
    private String descripcion;
    private String logoBase64;
}
