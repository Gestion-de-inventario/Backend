package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private Rol role;
    private Estado status = Estado.ACTIVO;
    private Persona persona;
}
