package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class Beneficiary {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private Estado status = Estado.ACTIVO;

    public Beneficiary() {
        return;
    }

    public Beneficiary(int id, String dni, String name, String lastname, Estado status){
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.lastname = lastname;
        this.status = status;
    }

    public int getId() { return id; }
    public String getDni() { return dni; }
    public String getName() { return name; }
    public String getLastname() { return lastname; }
    public Estado getStatus() { return status;}

    public Beneficiary update(String dni, String name, String lastname, Estado status ) {
        return new Beneficiary(this.id, dni, name, lastname, status);
    }

    public void marcarComoInactivo(){
        this.status = Estado.INACTIVO;
    }

    public void marcarComoActivo(){
        this.status = Estado.ACTIVO;
    }

    public void setId(int id) {
        this.id = id;
    }
}
