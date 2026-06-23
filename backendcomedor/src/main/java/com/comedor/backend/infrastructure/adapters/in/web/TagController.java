package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.ActivateTagUseCase;
import com.comedor.backend.application.ports.in.CreateTagUseCase;
import com.comedor.backend.application.ports.in.DeactivateTagUseCase;
import com.comedor.backend.application.ports.in.ListTagsByStatusUseCase;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.TagRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.TagResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {
    private final CreateTagUseCase createTagUseCase;
    private final ListTagsByStatusUseCase listTagsByStatusUseCase;
    private final ActivateTagUseCase activateTagUseCase;
    private final DeactivateTagUseCase deactivateTagUseCase;

    @PreAuthorize("hasAuthority('TAG_LIST_BY_STATUS')")
    @GetMapping("/list")
    public List<TagResponseDTO> listarEtiquetas(@RequestParam(required = false) Status status)
    {
        return listTagsByStatusUseCase.listarEtiquetas(status);
    }

    @PreAuthorize("hasAuthority('TAG_CREATE')")
    @PostMapping("/create")
    public TagResponseDTO crearEtiqueta(@RequestBody TagRequestDTO tagRequestDTO)
    {
        return createTagUseCase.crearEtiqueta(tagRequestDTO);
    }

    @PreAuthorize("hasAuthority('TAG_CHANGE_STATUS')")
    @PostMapping("/changeStatus/{id}")
    public TagResponseDTO cambiarEstado(@PathVariable int id, @RequestParam Status status)
    {

        if (status == null) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }

        return switch (status) {
            case ACTIVO -> activateTagUseCase.activarEtiquetaPorId(id);
            case INACTIVO -> deactivateTagUseCase.desactivarEtiquetaPorId(id);
        };
    }
}
