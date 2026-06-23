package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Status;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private int id;
    private String name;
    private Status status;
    private Set<Permission> permissions = new HashSet<>();

    public Role(int id,
                String name, Status status,
                Set<Permission> permissions) {

        this.id = id;
        this.name = name.toUpperCase();
        this.status = status;
        this.permissions = permissions;
    }
    public Role() {

    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name.toUpperCase();
    }

    public Status getStatus() {
        return status;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
