package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

public class User {
    private Integer id;
    private String username;
    private String password;
    private Role role;
    private Estado status = Estado.ACTIVO;
    private Person person;

    public User(Integer id, String username, String password, Role role, Estado status, Person person) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.person = person;
    }
    public User() {}

    public Person getPersona() { return person;}

    public Integer getId() { return id;}

    public Role getRol() { return role;}

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public Estado getStatus() {
        return status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(Estado status) {
        this.status = status;
    }

    public void setPersona(Person person) {
        this.person = person;
        person.setUser(this);
    }
}
