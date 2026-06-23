package com.comedor.backend.infrastructure.adapters.in.web.dto.request;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private String lastname;
    private String dni;
    private String password;
    private Integer role_id;
}
