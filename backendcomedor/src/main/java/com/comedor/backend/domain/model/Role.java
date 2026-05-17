package com.comedor.backend.domain.model;

public class Role {
    private int id;
    private String nombre;
    public Role(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public String getName() {
        return nombre;
    }
    public int getId() {
        return id;
    }
}
