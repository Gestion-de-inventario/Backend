package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatosPersonalesResponseDTO {
    private String dni;
    private String names;
    private String lastnames;
}
