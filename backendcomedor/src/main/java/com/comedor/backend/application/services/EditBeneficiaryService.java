package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RegisterAuditUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInactiveException;
import com.comedor.backend.domain.exceptions.DniAlreadyRegisteredException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.AuditAction;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.AuditRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditBeneficiaryRequestDTO;

import java.util.Map;

public class EditBeneficiaryService implements EditBeneficiaryUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterAuditUseCase registerAuditUseCase;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    public EditBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterAuditUseCase registerAuditUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerAuditUseCase = registerAuditUseCase;
        this.beneficiaryTypeRepositoryPort = beneficiaryTypeRepositoryPort;
    }

    @Override
    public Beneficiary editar(int id, EditBeneficiaryRequestDTO editarBeneficiarioRequest) {
        Beneficiary beneficiary = beneficiaryRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficiaryNotFoundException("Usuario No Encontrado: " + id));



        if (editarBeneficiarioRequest.getDni() != null && !beneficiary.getDni().equals(editarBeneficiarioRequest.getDni())) {
            if (beneficiaryRepositoryPort.existePorDni(editarBeneficiarioRequest.getDni())) {
                throw new DniAlreadyRegisteredException("Ya existe un Beneficiario con el DNI: " + editarBeneficiarioRequest.getDni());
            }
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Beneficiario",
                            beneficiary.getId(),
                            beneficiary.getName()
                                    .concat(" " + beneficiary.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "DNI",
                                    "previousValue", beneficiary.getDni(),
                                    "newValue",
                                    editarBeneficiarioRequest.getDni()
                            )
                    )
            );
            beneficiary.setDni(editarBeneficiarioRequest.getDni());
        }

        if (editarBeneficiarioRequest.getName() != null && !editarBeneficiarioRequest.getName().equalsIgnoreCase(beneficiary.getName())) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Beneficiario",
                            beneficiary.getId(),
                            beneficiary.getName()
                                    .concat(" " + beneficiary.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "nombre",
                                    "previousValue", beneficiary.getName(),
                                    "newValue",
                                    editarBeneficiarioRequest.getName()
                            )
                    )
            );

            beneficiary.setName(editarBeneficiarioRequest.getName()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getLastname() != null && !editarBeneficiarioRequest.getLastname().equalsIgnoreCase(beneficiary.getLastname())) {
            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Beneficiario",
                            beneficiary.getId(),
                            beneficiary.getName()
                                    .concat(" " + beneficiary.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute", "apellido",
                                    "previousValue",
                                    beneficiary.getLastname(),
                                    "newValue",
                                    editarBeneficiarioRequest.getLastname()
                            )
                    )
            );
            beneficiary.setLastname(editarBeneficiarioRequest.getLastname()); // ✅ Agregar
        }

        if(editarBeneficiarioRequest.getBeneficiaryTypeId() != null && !editarBeneficiarioRequest.getBeneficiaryTypeId().equals(beneficiary.getBeneficiaryType().getId()))
        {   BeneficiaryType newBeneficiaryType  = beneficiaryTypeRepositoryPort.findById(editarBeneficiarioRequest.getBeneficiaryTypeId());

            if(newBeneficiaryType .getStatus().equals(Status.INACTIVO))
            {
                throw new BeneficiaryTypeInactiveException("No puedes elegir un tipo inactivo. El tipo de beneficiario : "+ newBeneficiaryType .getName()+" ,esta inactivo.");
            }

            registerAuditUseCase.registrar(
                    new AuditRequestDTO(
                            "Beneficiario",
                            beneficiary.getId(),
                            beneficiary.getName()
                                    .concat(" " + beneficiary.getLastname()),
                            AuditAction.MODIFICACION,
                            Map.of(
                                    "attribute",
                                    "Tipo de beneficiario",
                                    "previousValue",
                                    beneficiary.getBeneficiaryType().getName(),
                                    "newValue",
                                    newBeneficiaryType.getName()
                            )
                    )
            );
            beneficiary.setBeneficiaryType(newBeneficiaryType);
        }

        return beneficiaryRepositoryPort.guardar(beneficiary);
    }
}
