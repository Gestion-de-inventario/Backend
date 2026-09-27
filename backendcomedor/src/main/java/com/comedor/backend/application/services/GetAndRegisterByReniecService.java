package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.GetDataByDniUseCase;
import com.comedor.backend.application.ports.in.GetAndRegisterByReniecUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryAlreadyRegisteredException;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.PersonalDataReniec;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;

import java.util.Map;

public class GetAndRegisterByReniecService implements GetAndRegisterByReniecUseCase {


    private final GetDataByDniUseCase getDataByDniUseCase;
    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;

    public GetAndRegisterByReniecService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, GetDataByDniUseCase getDataByDniUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort, RegisterAuditUseCase registerAuditUseCase) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.getDataByDniUseCase = getDataByDniUseCase;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
    }

    @Override
    public Beneficiary consultarYRegistrar(String dni) {
        if (beneficiaryRepositoryPort.existePorDni(dni)) {
            throw new BeneficiaryAlreadyRegisteredException(
                    "Ya existe un beneficiario registrado con el DNI " + dni
            );
        }

        PersonalDataReniec personalDataReniec =
                getDataByDniUseCase.consultar(dni);

        BeneficiaryType defaultType =
                beneficiaryTypeRepositoryPort.findById(1);

        Beneficiary beneficiary = new Beneficiary(
                0,
                personalDataReniec.getDni(),
                personalDataReniec.getNames(),
                personalDataReniec.getLastnames(),
                Status.ACTIVO,
                defaultType
        );

        Beneficiary saved = beneficiaryRepositoryPort.guardar(beneficiary);

        registerAuditUseCase.registrar(
                new AuditRequestDTO(
                        "Beneficiario",
                        saved.getId(),
                        saved.getName()
                                .concat(" " + saved.getLastname()),
                        AuditAction.CREACION,
                        Map.of(
                                "DNI", saved.getDni(),
                                "Nombre", saved.getName(),
                                "Apellido", saved.getLastname(),
                                "Tipo de beneficiario", saved.getBeneficiaryType().getName()
                        )
                )
        );
        return saved;
    }
}
