package com.comedor.backend.domain.model;

import com.comedor.backend.domain.model.enums.ModuleCode;
import com.comedor.backend.domain.model.enums.PermissionCode;

import java.util.Objects;

public class Permission {
    private Integer id;
    private String description;
    private PermissionCode code;
    private ModuleCode module;

    public Permission(Integer id,
                      String description,
                      PermissionCode code,
                      ModuleCode module) {

        this.id = id;
        this.description = description;
        this.code = code;
        this.module = module;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public PermissionCode getCode() {
        return code;
    }

    public ModuleCode getModule() {
        return module;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Permission that)) return false;

        return code == that.code;
    }
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
