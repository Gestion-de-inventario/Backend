package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class PersonaResponseDTO {
    private int id;
    private String name;
    private String lastname;
    private String dni;
}
