package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

@Data
public class PersonResponseDTO {
    private int id;
    private String name;
    private String lastname;
    private String dni;
}
