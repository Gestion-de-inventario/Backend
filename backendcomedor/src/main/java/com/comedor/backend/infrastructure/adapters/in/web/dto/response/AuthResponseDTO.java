package com.comedor.backend.infrastructure.adapters.in.web.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AuthResponseDTO {
    private Integer id;
    private String token;
    private String name;
    private String lastname;
    private String role;
    private List<String> permissions;
}