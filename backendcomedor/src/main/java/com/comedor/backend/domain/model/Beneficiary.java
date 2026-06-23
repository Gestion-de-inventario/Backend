package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Status;

public class Beneficiary {
    private int id;
    private String dni;
    private String name;
    private String lastname;
    private BeneficiaryType beneficiaryType;
    private Status status = Status.ACTIVO;

    public Beneficiary() {
        return;
    }

    public Beneficiary(int id, String dni, String name, String lastname, Status status, BeneficiaryType beneficiaryType){
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
    public Status getStatus() { return status;}

    public Beneficiary update(String dni, String name, String lastname, Status status , BeneficiaryType beneficiaryType) {
        return new Beneficiary(this.id, dni, name, lastname, status,beneficiaryType);
    }

    public void marcarComoInactivo(){
        this.status = Status.INACTIVO;
    }

    public void marcarComoActivo(){
        this.status = Status.ACTIVO;
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

    public void setStatus(Status status) {
        this.status = status;
    }

    public BeneficiaryType getBeneficiaryType() {
        return beneficiaryType;
    }

    public void setBeneficiaryType(BeneficiaryType beneficiaryType) {
        this.beneficiaryType = beneficiaryType;
    }

    @Override
    public String toString() {
        return "Beneficiary{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", beneficiaryType=" + beneficiaryType +
                ", status=" + status +
                '}';
    }
}
