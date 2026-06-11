package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.*;
import com.comedor.backend.domain.model.enums.CambioEstado;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.PermissionsAsigmentRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.RolResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;

    private final EditRoleUseCase editRoleUseCase;

    private final ListRoleByStatusUseCase listRoleByStatusUseCase;

    private final ListRoleByIdUseCase listRoleByIdUseCase;

    private final AssignPermissionsUseCase assignPermissionsUseCase;

    private final RoleChangeStatusUseCase roleChangeStatusUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RolResponseDTO> createRole(
            @RequestBody CreateRoleRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createRoleUseCase.createRole(dto));
    }

    @PutMapping("edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_EDIT')")
    public ResponseEntity<RolResponseDTO> editRole(
            @PathVariable int id,
            @RequestBody EditRoleRequestDTO dto
            ) {

        return ResponseEntity.ok(
                editRoleUseCase.editRole(id, dto)
        );
    }

    @PutMapping("changeStatus/{id}")
    @PreAuthorize("hasAuthority('ROLE_CHANGE_STATUS')")
    public ResponseEntity<RolResponseDTO> changeStatus(
            @PathVariable int id,
            @RequestParam() CambioEstado status) {

        return ResponseEntity.ok(
                roleChangeStatusUseCase.changeStatusById(id, status)
        );
    }

    @PutMapping("assignPermissions/{id}")
    @PreAuthorize("hasAuthority('ROLE_ASSIGN_PERMISSIONS')")
    public ResponseEntity<RolResponseDTO> assignPermissions(
            @PathVariable int id,
            @RequestBody PermissionsAsigmentRequestDTO dto) {

        return ResponseEntity.ok(
                assignPermissionsUseCase.assignPermissions(id, dto)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIST_BY_STATUS')")
    public ResponseEntity<List<RolResponseDTO>>
    listRolesByStatus(
            @RequestParam(required = false)  Estado status) {

        return ResponseEntity.ok(
                listRoleByStatusUseCase
                        .listRolesByStatus(status)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_GET_BY_ID')")
    public ResponseEntity<RolResponseDTO>
    getRoleById(
            @PathVariable int id) {

        return ResponseEntity.ok(
                listRoleByIdUseCase.getRoleById(id)
        );
    }
}