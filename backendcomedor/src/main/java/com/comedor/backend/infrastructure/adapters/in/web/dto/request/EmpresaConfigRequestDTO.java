package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class EmpresaConfigRequestDTO {
    private String nombre;
    private String descripcion;
    private String logoBase64;
}
