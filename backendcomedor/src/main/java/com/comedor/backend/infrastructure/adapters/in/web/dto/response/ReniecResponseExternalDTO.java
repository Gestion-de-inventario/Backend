package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

@Data
public class ReniecResponseExternalDTO {
    private String nombres;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String numeroDocumento;
}
