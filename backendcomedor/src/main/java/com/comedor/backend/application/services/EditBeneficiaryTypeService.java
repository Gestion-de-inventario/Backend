package com.comedor.backend.application.services;

import com.comedor.backend.application.common.mapper.BeneficiaryTypeMapper;
import com.comedor.backend.application.ports.in.EditBeneficiaryTypeUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeAlreadyExistsException;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.BeneficiaryTypeRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.response.BeneficiaryTypeResponseDTO;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class EditBeneficiaryTypeService implements EditBeneficiaryTypeUseCase {
    private final BeneficiaryTypeRepositoryPort repository;
    private final BeneficiaryTypeMapper mapper;
    private final RegisterAuditUseCase registerAuditUseCase;

    public EditBeneficiaryTypeService(BeneficiaryTypeRepositoryPort repository, BeneficiaryTypeMapper mapper, RegisterAuditUseCase registerAuditUseCase) {
        this.repository = repository;
        this.mapper = mapper;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public BeneficiaryTypeResponseDTO editBeneficiaryType(
            Integer id,
            BeneficiaryTypeRequestDTO request
    ) {

        BeneficiaryType beneficiaryType =
                repository.findById(id);

        String name = request.getName() != null
                ? request.getName()
                : beneficiaryType.getName();

        String desc = request.getDesc() != null
                ? request.getDesc()
                : beneficiaryType.getDesc();

        BigDecimal menuCost = request.getMenu_cost() != null
                ? request.getMenu_cost()
                : beneficiaryType.getMenu_cost();

        String normalizedName = name.toUpperCase();

        if (!beneficiaryType.getName().equalsIgnoreCase(normalizedName)
                && repository.existsByNameAndIdNot(normalizedName, id)
        ) {
            throw new BeneficiaryTypeAlreadyExistsException(
                    "Ya existe un tipo de beneficiario con el nombre : " + normalizedName
            );
        }

        if(!beneficiaryType.getName().equalsIgnoreCase(normalizedName)) {

            registrarCambio(
                    beneficiaryType.getId(),
                    beneficiaryType.getName(),
                    "nombre",
                    beneficiaryType.getName(),
                    normalizedName
            );

            beneficiaryType.setName(name);
        }

        if (!Objects.equals(
                beneficiaryType.getDesc(),
                desc
        )) {

            registrarCambio(
                    beneficiaryType.getId(),
                    beneficiaryType.getName(),
                    "descripción",
                    beneficiaryType.getDesc(),
                    desc
            );

            beneficiaryType.setDesc(desc);
        }

        if(
                beneficiaryType.getMenu_cost()
                        .compareTo(menuCost) != 0
        ) {

            registrarCambio(
                    beneficiaryType.getId(),
                    beneficiaryType.getName(),
                    "costo menú",
                    beneficiaryType.getMenu_cost().toString(),
                    menuCost.toString()
            );

            beneficiaryType.setMenu_cost(menuCost);
        }

        return mapper.convertToDTO(
                repository.update(beneficiaryType)
        );
    }

    private void registrarCambio(
            Integer entityId,
            String nombre,
            String campo,
            String valorAnterior,
            String valorNuevo) {

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Tipo de beneficiario",
                        entityId,
                        nombre,
                        AuditAction.MODIFICACION,
                        Map.of(
                                "attribute", campo,
                                "previousValue",
                                valorAnterior != null ? valorAnterior : "-",
                                "newValue",
                                valorNuevo != null ? valorNuevo : "-"
                        )
                )
        );
    }
}
