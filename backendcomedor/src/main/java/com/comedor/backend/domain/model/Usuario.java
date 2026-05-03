package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class Usuario {
    private Integer id;
    private String username;
    private String password;
    private Rol role;
    private Estado status = Estado.ACTIVO;
    private Persona persona;

    public Usuario(Integer id, String username, String password, Rol role, Estado status, Persona persona) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.persona = persona;
    }

    public Persona getPersona() { return persona;}

    public Integer getId() { return id;}

    public Rol getRol() { return role;}

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Estado getStatus() {
        return status;
    }

}
