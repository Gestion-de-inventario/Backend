package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;
import lombok.Getter;

public class Beneficiario {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private Estado status = Estado.ACTIVO;

    public Beneficiario() {
        return;
    }

    public Beneficiario(int id, String dni, String name, String lastname, Estado status){
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


    public void marcarComoInactivo(){
        this.status = status.INACTIVO;
    }

    public void marcarComoActivo(){
        this.status = status.ACTIVO;
    }

}
