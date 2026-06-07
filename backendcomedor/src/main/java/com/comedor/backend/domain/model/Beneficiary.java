package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class Beneficiary {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private BeneficiaryType beneficiaryType;
    private Estado status = Estado.ACTIVO;

    public Beneficiary() {
        return;
    }

    public Beneficiary(int id, String dni, String name, String lastname, Estado status,BeneficiaryType beneficiaryType){
        this.id = id;
        this.dni = dni;
        this.name = name;
        this.lastname = lastname;
        this.status = status;
        this.beneficiaryType = beneficiaryType;
    }

    public int getId() { return id; }
    public String getDni() { return dni; }
    public String getName() { return name; }
    public String getLastname() { return lastname;}
    public Estado getStatus() { return status;}

    public Beneficiary update(String dni, String name, String lastname, Estado status ,BeneficiaryType beneficiaryType) {
        return new Beneficiary(this.id, dni, name, lastname, status,beneficiaryType);
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

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public BeneficiaryType getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(BeneficiaryType beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }
}
