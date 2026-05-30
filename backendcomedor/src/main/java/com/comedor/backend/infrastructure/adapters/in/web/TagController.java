package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ActivarEtiquetaUseCase;
import com.comedor.backend.application.ports.in.CrearEtiquetaUseCase;
import com.comedor.backend.application.ports.in.DesactivarEtiquetaUseCase;
import com.comedor.backend.application.ports.in.ListarEtiquetasPorEstadoUseCase;
import com.comedor.backend.domain.model.enums.Estado;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EtiquetaRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.EtiquetaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final CrearEtiquetaUseCase crearEtiquetaUseCase;
    private final ListarEtiquetasPorEstadoUseCase listarEtiquetasPorEstadoUseCase;
    private final ActivarEtiquetaUseCase activarEtiquetaUseCase;
    private final DesactivarEtiquetaUseCase desactivarEtiquetaUseCase;

    @PreAuthorize("hasAuthority('TAG_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<EtiquetaResponseDTO> listarEtiquetas(@RequestParam(required = false) Estado estado)
    {
        return listarEtiquetasPorEstadoUseCase.listarEtiquetas(estado);
    }

    @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public EtiquetaResponseDTO crearEtiqueta(@RequestBody EtiquetaRequestDTO etiquetaRequestDTO)
    {
        return crearEtiquetaUseCase.crearEtiqueta(etiquetaRequestDTO);
    }

    @PreAuthorize("hasAuthority('TAG_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public EtiquetaResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Estado estado)
    {

        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (estado) {
            case ACTIVO -> activarEtiquetaUseCase.activarEtiquetaPorId(id);
            case INACTIVO -> desactivarEtiquetaUseCase.desactivarEtiquetaPorId(id);
        };
    }
}
