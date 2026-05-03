package com.comedor.backend.domain.model;

public class Rol {
    private int id;
    private String nombre;
    public Rol (int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    public String getNombre() {
        return nombre;
    }
    public int getId() {
        return id;
    }
}
