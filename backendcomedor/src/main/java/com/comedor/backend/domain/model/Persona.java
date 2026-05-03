package com.comedor.backend.domain.model;

public class Persona {
    private int id;
    private String name;
    private String lastname;
    private String dni;
    private Usuario user;

    public Persona(int id, String name, String lastname, String dni) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
    }
    public Persona() {}

    public String getName (){
        return name;
    }
    public String getLastname (){
        return lastname;
    }

    public int getId() {
        return id;
    }

    public String getDni() {
        return dni;
    }

    public Usuario getUser() {
        return user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }
}
