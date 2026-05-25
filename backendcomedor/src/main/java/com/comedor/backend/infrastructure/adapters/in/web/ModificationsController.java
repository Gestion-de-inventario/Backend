package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ListarModificacionesUseCase;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.ModificationsResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/modifications")
@RequiredArgsConstructor
public class ModificationsController {

    private final ListarModificacionesUseCase listarModificacionesUseCase;


    @PreAuthorize("hasAuthority('MODIFICATION_LIST_ALL')")
    @GetMapping()
    public ResponseEntity<Page<ModificationsResponseDTO>> list(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(listarModificacionesUseCase.list(page,size));
    }
}
