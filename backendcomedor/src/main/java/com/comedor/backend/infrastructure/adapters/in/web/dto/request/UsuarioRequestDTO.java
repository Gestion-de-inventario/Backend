package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class UsuarioRequestDTO {
    private String name;
    private String lastname;
    private String dni;
    private String password;
}
