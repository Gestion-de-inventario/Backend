package com.comedor.backend.application.common.mapper;

import com.comedor.backend.domain.model.Permission;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PermissionResponseDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class PermissionMapper {

    public PermissionResponseDTO toResponseDTO(
            Permission permission) {

        if (permission == null) {
            return null;
        }

        PermissionResponseDTO dto =
                new PermissionResponseDTO();

        dto.setDescription(
                permission.getDescription()
        );

        dto.setCode(
                permission.getCode()
        );

        dto.setModule(
                permission.getModule()
        );

        return dto;
    }

    public List<PermissionResponseDTO>
    toResponseList(List<Permission> permissions) {

        if (permissions == null) {
            return Collections.emptyList();
        }

        return permissions.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<PermissionResponseDTO>
    toResponseList(Set<Permission> permissions) {

        if (permissions == null) {
            return Collections.emptyList();
        }

        return permissions.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
