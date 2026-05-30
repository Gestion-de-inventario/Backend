package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

@Data
public class UsuarioBasicoDTO {
    private Integer id;
    private String name;
    private String username;

    public UsuarioBasicoDTO(Integer id, String name, String username) {
        this.id = id;
        this.name = name;
        this.username = username;
    }
}
