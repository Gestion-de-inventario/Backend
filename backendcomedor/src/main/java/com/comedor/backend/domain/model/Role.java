package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.Estado;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private int id;
    private String name;
    private Estado status;
    private Set<Permission> permissions = new HashSet<>();

    public Role(int id,
                String name,Estado status,
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
        return name;
    }

    public Estado getStatus() {
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

    public void setStatus(Estado status) {
        this.status = status;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
