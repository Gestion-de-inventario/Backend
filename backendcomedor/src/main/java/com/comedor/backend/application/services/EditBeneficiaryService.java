package com.comedor.backend.application.services;

import com.comedor.backend.application.ports.in.EditBeneficiaryUseCase;
import com.comedor.backend.application.ports.in.RegisterModificationUseCase;
import com.comedor.backend.application.ports.out.BeneficiaryRepositoryPort;
import com.comedor.backend.application.ports.out.BeneficiaryTypeRepositoryPort;
import com.comedor.backend.domain.exceptions.BeneficiaryNotFoundException;
import com.comedor.backend.domain.exceptions.BeneficiaryTypeInactiveException;
import com.comedor.backend.domain.exceptions.DniAlreadyRegisteredException;
import com.comedor.backend.domain.model.Beneficiary;
import com.comedor.backend.domain.model.BeneficiaryType;
import com.comedor.backend.domain.model.enums.Status;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.EditBeneficiaryRequestDTO;
import com.comedor.backend.infrastructure.adapters.in.web.dto.request.ModificationsRequestDTO;

public class EditBeneficiaryService implements EditBeneficiaryUseCase {

    private final BeneficiaryRepositoryPort beneficiaryRepositoryPort;
    private final RegisterModificationUseCase registerModificationUseCase;
    private final BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort;

    public EditBeneficiaryService(BeneficiaryRepositoryPort beneficiaryRepositoryPort, RegisterModificationUseCase registerModificationUseCase, BeneficiaryTypeRepositoryPort beneficiaryTypeRepositoryPort) {
        this.beneficiaryRepositoryPort = beneficiaryRepositoryPort;
        this.registerModificationUseCase = registerModificationUseCase;
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
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario",
                    beneficiary.getName().concat(" "+beneficiary.getLastname())
                    , "dni", beneficiary.getDni(), editarBeneficiarioRequest.getDni()
            ));
            beneficiary.setDni(editarBeneficiarioRequest.getDni());
        }

        if (editarBeneficiarioRequest.getName() != null && !editarBeneficiarioRequest.getName().equalsIgnoreCase(beneficiary.getName())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario",
                    beneficiary.getName().concat(" "+beneficiary.getLastname())
                    ,"name", beneficiary.getName(), editarBeneficiarioRequest.getName()
            ));
            beneficiary.setName(editarBeneficiarioRequest.getName()); // ✅ Agregar
        }

        if (editarBeneficiarioRequest.getLastname() != null && !editarBeneficiarioRequest.getLastname().equalsIgnoreCase(beneficiary.getLastname())) {
            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario",
                    beneficiary.getName().concat(" "+beneficiary.getLastname()),
                    "lastname", beneficiary.getLastname(), editarBeneficiarioRequest.getLastname()
            ));
            beneficiary.setLastname(editarBeneficiarioRequest.getLastname()); // ✅ Agregar
        }

        if(editarBeneficiarioRequest.getBeneficiaryTypeId() != null && !editarBeneficiarioRequest.getBeneficiaryTypeId().equals(beneficiary.getBeneficiaryType().getId()))
        {   BeneficiaryType newBeneciaryType = beneficiaryTypeRepositoryPort.findById(editarBeneficiarioRequest.getBeneficiaryTypeId());

            if(newBeneciaryType.getStatus().equals(Status.INACTIVO))
            {
                throw new BeneficiaryTypeInactiveException("No puedes elegir un tipo inactivo. El tipo de beneficiario : "+ newBeneciaryType.getName()+" ,esta inactivo.");
            }

            registerModificationUseCase.registrar(new ModificationsRequestDTO(
                    "Beneficiario", "Tipo de beneficiario",
                    beneficiary.getName().concat(" "+beneficiary.getLastname()),
                    beneficiary.getBeneficiaryType().getName(),
                    newBeneciaryType.getName()
            ));
            beneficiary.setBeneficiaryType(newBeneciaryType);
        }

        return beneficiaryRepositoryPort.guardar(beneficiary);
    }
}
