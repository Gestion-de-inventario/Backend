package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.CreateRoleUseCase;
import com.comedor.backend.application.ports.in.EditRoleUseCase;
import com.comedor.backend.application.ports.in.ListRoleByIdUseCase;
import com.comedor.backend.application.ports.in.ListRoleByStatusUseCase;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.CreateRoleRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditRoleRequestDTO;
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

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<RolResponseDTO> createRole(
            @RequestBody CreateRoleRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createRoleUseCase.createRole(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EDIT')")
    public ResponseEntity<RolResponseDTO> editRole(
            @PathVariable int id,
            @RequestBody EditRoleRequestDTO dto) {

        return ResponseEntity.ok(
                editRoleUseCase.editRole(id, dto)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_LIST_BY_STATUS')")
    public ResponseEntity<List<RolResponseDTO>>
    listRolesByStatus(
            @RequestParam Estado status) {

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