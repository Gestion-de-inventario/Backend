package com.comedor.backend.infrastructure.adapters.in.web;

import com.comedor.backend.application.ports.in.UpdateCompanyConfigUseCase;
import com.comedor.backend.application.ports.in.ObtenerEmpresaConfigUseCase;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EmpresaConfigRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/empresa-config")
@RequiredArgsConstructor
public class EmpresaConfigController {

    private final ObtenerEmpresaConfigUseCase obtenerEmpresaConfigUseCase;
    private final UpdateCompanyConfigUseCase updateCompanyConfigUseCase;

    @PreAuthorize("hasAuthority('EMPRESA_CONFIG_VIEW')")
    @GetMapping
    public EmpresaConfig obtener() {
        return obtenerEmpresaConfigUseCase.obtener();
    }

    @PreAuthorize("hasAuthority('EMPRESA_CONFIG_EDIT')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EmpresaConfig actualizar(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "logo", required = false) MultipartFile logoFile
    ) {
        EmpresaConfigRequestDTO request = new EmpresaConfigRequestDTO();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);

        // Si el usuario subió un archivo, el backend lo convierte a Base64 por él
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String base64Image = Base64.getEncoder().encodeToString(logoFile.getBytes());
                request.setLogoBase64(base64Image);
            } catch (IOException e) {
                throw new RuntimeException("Error al procesar la imagen del logo", e);
            }
        }

        return updateCompanyConfigUseCase.actualizar(request);
    }

}
