package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.services.ListAllPermissionsService;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.PermissionResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final ListAllPermissionsService listAllPermissionsService;

    @PreAuthorize("hasAuthority('PERMISSION_LIST_ALL')")
    @GetMapping("/all")
    public List<PermissionResponseDTO> listAllPermissions() {
        return listAllPermissionsService.listAllPermissions();
    }

}
