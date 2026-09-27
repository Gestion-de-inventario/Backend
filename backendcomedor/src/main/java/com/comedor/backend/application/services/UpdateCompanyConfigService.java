package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.in.UpdateCompanyConfigUseCase;
import com.comedor.backend.application.ports.out.EmpresaConfigRepositoryPort;
import com.comedor.backend.domain.model.EmpresaConfig;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EmpresaConfigRequestDTO;

import java.util.Map;

public class UpdateCompanyConfigService implements UpdateCompanyConfigUseCase {

    private final EmpresaConfigRepositoryPort repository;
    private final RegisterAuditUseCase registerAuditUseCase;
    public UpdateCompanyConfigService(EmpresaConfigRepositoryPort repository, RegisterAuditUseCase registerAuditUseCase) {
        this.repository = repository;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public EmpresaConfig actualizar(EmpresaConfigRequestDTO request) {
        EmpresaConfig config = repository.obtener();

        String entityName = config.getNombre();

        if (request.getNombre() != null && !request.getNombre().isBlank()) {

            String nuevoNombre = request.getNombre().trim().toUpperCase();

            if (!nuevoNombre.equals(config.getNombre())) {

                registrarCambio(
                        config.getId(),
                        entityName,
                        "nombre",
                        config.getNombre(),
                        nuevoNombre
                );

                config.setNombre(nuevoNombre);
            }
        }

        if (request.getDescripcion() != null) {

            if (!request.getDescripcion().equals(config.getDescripcion())) {

                registrarCambio(
                        config.getId(),
                        entityName,
                        "descripción",
                        config.getDescripcion(),
                        request.getDescripcion()
                );

                config.setDescripcion(request.getDescripcion());
            }
        }

        if (request.getLogoBase64() != null) {

            if (!request.getLogoBase64().equals(config.getLogoBase64())) {

                registrarCambio(
                        config.getId(),
                        entityName,
                        "logo",
                        config.getLogoBase64() != null
                                ? "Logo anterior"
                                : "Sin logo",
                        "Logo actualizado"
                );

                config.setLogoBase64(request.getLogoBase64());
            }
        }

        return repository.guardar(config);
    }

    private void registrarCambio(
            Integer entityId,
            String entityName,
            String attribute,
            String previousValue,
            String newValue) {

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Configuración de Empresa",
                        entityId,
                        entityName,
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", attribute,
                                "previousValue",
                                previousValue != null
                                        ? previousValue
                                        : "-",
                                "newValue",
                                newValue != null
                                        ? newValue
                                        : "-"
                        )
                )
        );
    }
}
